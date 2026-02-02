package org.dotspace.oofp.controller;

import org.dotspace.oofp.model.dto.behaviorstep.StepContext;
import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.model.dto.eip.InboundEnvelope;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchAttrKeys;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchRequest;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchResult;
import org.dotspace.oofp.service.formreview.FormReviewService;
import org.dotspace.oofp.utils.builder.GeneralBuilders;
import org.dotspace.oofp.utils.builder.operation.WriteOperations;
import org.dotspace.oofp.utils.eip.flow.FormReviewInboundFlows;
import org.dotspace.oofp.utils.eip.auth.EntitlementsResolver;
import org.dotspace.oofp.utils.eip.flow.InboundFlow;
import org.dotspace.oofp.utils.eip.inbound.InboundAdapters;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/forms")
public class FormReviewController {

    private final InboundFlow<ItemSearchRequest> inboundFlow;

    public FormReviewController(
            FormReviewService service, Supplier<Validation<Violations, Authentication>> authSupplier,
            EntitlementsResolver entitlementsResolver) {
        this.inboundFlow = FormReviewInboundFlows.itemSearchInboundFlow(service, authSupplier, entitlementsResolver);
    }

    /**
     * POST /api/forms/{formSn}/groups/{groupId}/items/search
     */
    @PostMapping("/{formSn}/groups/{groupId}/items/search")
    public ResponseEntity<?> searchItems(
            HttpServletRequest request,
            @RequestBody ItemSearchRequest body) {
        // ✅ 唯一入口：POJO + HTTP → InboundEnvelope
        InboundEnvelope<ItemSearchRequest> envelope =
                InboundAdapters.fromHttp(request, body);

        Validation<Violations, StepContext<ItemSearchRequest>> result = inboundFlow.from(envelope);

        return result.fold(v -> {
            // 處理驗證錯誤
            return ResponseEntity
                    .status(mapStatus(v))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(GeneralBuilders.supply(ItemSearchResult::new)
                            .with(WriteOperations.set(ItemSearchResult::setRequest, body))
                            .with(WriteOperations.set(ItemSearchResult::setResultCode, "E999"))
                            .with(WriteOperations.set(ItemSearchResult::setResultDetails, toProblemJson(v)))
                            .with(WriteOperations.set(ItemSearchResult::setItems, List.of()))
                            .build());
        }, sc -> {
            // 處理成功結果
            return ResponseEntity.ok(
                    GeneralBuilders.supply(ItemSearchResult::new)
                            .with(WriteOperations.set(ItemSearchResult::setRequest, sc.getPayload()))
                            .with(WriteOperations.set(ItemSearchResult::setResultCode, "S000"))
                            .with(WriteOperations.set(ItemSearchResult::setResultDetails, Map.of()))
                            .with(WriteOperations.set(ItemSearchResult::setItems,
                                    ItemSearchAttrKeys.ITEM_SEARCH_RESULT
                                            .maybe(sc)
                                            .map(ItemSearchResult::getItems)
                                            .orElse(List.of())))
                            .build()
            );
        });
    }

    // ---- error mapping（仍然只在 Controller） ----

    private int mapStatus(Violations v) {
        if (v.namesAnyMatch(name -> Stream.of(
                "auth.ctx.", "auth-binding.unauthorized")
                .anyMatch(name::startsWith))) {
            return 401;
        }

        if (v.namesAnyMatch(name -> name.startsWith("auth-binding.entitlements."))) {
            return 403;
        }

        if (v.namesAnyMatch(name ->
                name.startsWith("path.")
                        || name.startsWith("query.")
                        || name.startsWith("body.")
        )) {
            return 400;
        }

        return 422;
    }

    private Map<String, Object> toProblemJson(Violations v) {
        return Map.of(
                "title", "Inbound validation failed",
                "detail", v.collectMessages(),
                "violations", v
        );
    }
}
