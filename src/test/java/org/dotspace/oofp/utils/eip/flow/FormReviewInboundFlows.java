package org.dotspace.oofp.utils.eip.flow;

import org.dotspace.oofp.model.dto.auth.AuthContext;
import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.model.dto.itemsearch.ItemSearchRequest;
import org.dotspace.oofp.service.formreview.FormReviewService;
import org.dotspace.oofp.utils.builder.GeneralBuilders;
import org.dotspace.oofp.utils.builder.operation.WriteOperations;
import org.dotspace.oofp.utils.eip.auth.EntitlementsResolver;
import org.dotspace.oofp.utils.eip.inbound.AuthContextExtractors;
import org.dotspace.oofp.utils.eip.step.AuthBindingSteps;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

@UtilityClass
public class FormReviewInboundFlows {

    public InboundFlow<ItemSearchRequest> itemSearchInboundFlow(
            FormReviewService service, Supplier<Validation<Violations, Authentication>> authSupplier,
            EntitlementsResolver entitlementsResolver) {
        return envelope -> InboundFlows.stateless(envelope)
                .flatMap(AuthBindingSteps.<ItemSearchRequest>bindAuthContext(
                        AuthBindingSteps.AuthCondition.required(),
                        authSupplier,
                        FormReviewInboundFlows::getAuthContext)::execute)
                .flatMap(AuthBindingSteps.<ItemSearchRequest>resolveEntitlements(
                        AuthBindingSteps.AuthCondition.required(), entitlementsResolver)::execute)
                .flatMap(service::searchItems); // ✅ service 直接吃 StepContext
    }

    private static AuthContext getAuthContext(Authentication a) {
        return GeneralBuilders.supply(AuthContext::new)
                .with(WriteOperations.set(AuthContext::setPrincipalId,
                        AuthContextExtractors.principalId(a)))
                .with(WriteOperations.set(AuthContext::setAuthorities,
                        AuthContextExtractors.authorities(a)))
                .with(WriteOperations.set(AuthContext::setRoles,
                        AuthContextExtractors.rolesFromAuthorities(a)))
                .with(WriteOperations.set(AuthContext::setRoleGroups,
                        AuthContextExtractors.scopesFromAuthorities(a)))
                .build();
    }

}
