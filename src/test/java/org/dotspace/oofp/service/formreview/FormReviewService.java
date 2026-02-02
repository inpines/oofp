package org.dotspace.oofp.service.formreview;

import org.dotspace.oofp.model.dto.behaviorstep.StepContext;
import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchAttrKeys;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchRequest;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchResult;
import org.dotspace.oofp.utils.builder.GeneralBuilders;
import org.dotspace.oofp.utils.builder.operation.WriteOperations;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FormReviewService {

    public Validation<Violations, StepContext<ItemSearchRequest>> searchItems(
            StepContext<ItemSearchRequest> stepContext) {
        // Implement your search logic here
        StepContext<ItemSearchRequest> result = stepContext.withAttribute(
                ItemSearchAttrKeys.ITEM_SEARCH_RESULT, find(stepContext.getPayload()));
        return Validation.valid(result);
    }

    private ItemSearchResult find(ItemSearchRequest request) {
        // 示範回傳搜尋結果
        return GeneralBuilders.supply(ItemSearchResult::new)
                .with(WriteOperations.set(ItemSearchResult::setRequest, request))
                .with(WriteOperations.set(ItemSearchResult::setItems,
                        List.of(
                                Map.of(
                                        "itemId", "item1",
                                        "itemName", "Test Item 1"
                                ),
                                Map.of(
                                        "itemId", "item2",
                                        "itemName", "Test Item 2"
                                )
                        )
                ))
                .build();
    }

}
