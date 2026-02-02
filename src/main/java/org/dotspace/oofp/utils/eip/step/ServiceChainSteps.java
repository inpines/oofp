package org.dotspace.oofp.utils.eip.step;

import org.dotspace.oofp.model.dto.behaviorstep.StepContext;
import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.utils.dsl.BehaviorStep;
import org.dotspace.oofp.utils.eip.AttrKey;
import org.dotspace.oofp.utils.eip.attr.ServiceRequestExtractor;
import org.dotspace.oofp.utils.functional.Extractor;
import org.dotspace.oofp.utils.functional.monad.Maybe;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class ServiceChainSteps {

    public <T, R> BehaviorStep<T> serviceStep(
            Function<T, Validation<Violations, R>> serviceOperation, AttrKey<R> attrKey) {
        return stepContext -> {
            Validation<Violations, R> result = serviceOperation.apply(stepContext.getPayload());
            return result.map(r -> stepContext.withAttribute(attrKey, r));
        };
    }

    public <T> BehaviorStep<T> require(
            Predicate<StepContext<T>> predicate,
            Extractor<StepContext<T>, Violations> violationsExtractor) {

        return sc -> Validation.<Violations, StepContext<T>>valid(sc)
                .filter(predicate, () -> violationsExtractor.extract(sc));
    }

    public <T, C, R> BehaviorStep<T> serviceStep(
            ServiceRequestExtractor<T, C> requestExtractor,
            Function<C, R> serviceOperation, AttrKey<R> attrKey) {

        return stepContext -> {
            Validation<Violations, C> ctx = safeExtract(requestExtractor, stepContext);

            return ctx.flatMap(c -> {
                try {
                    R result = serviceOperation.apply(c);
                    return Validation.valid(stepContext.withAttribute(attrKey, result));
                } catch (Exception ex) {
                    String msg = getExMsg(ex);
                    return Validation.invalid(Violations.violate("service.step.failed",
                            "Service operation failed: " + msg));
                }
            });
        };
    }

    private String getExMsg(Exception ex) {
        return ex.getClass().getSimpleName() + Maybe.given(ex.getMessage())
                .map(m -> ": " + m)
                .orElse(StringUtils.EMPTY);
    }

    private <T, C> Validation<Violations, C> safeExtract(
            ServiceRequestExtractor<T, C> requestExtractor, StepContext<T> stepContext) {
        try {
            C ctx = requestExtractor.extract(stepContext.getPayload(), stepContext::findAttribute);
            return Validation.valid(ctx);
        } catch (Exception ex) {
            String msg = getExMsg(ex);
            return Validation.invalid(Violations.violate("service.request.extract.failed",
                    "Service request extraction failed: " + msg));
        }
    }

    public <T, C, R> BehaviorStep<T> serviceValidationStep(
            ServiceRequestExtractor<T, C> requestExtractor,
            Function<C, Validation<Violations, R>> serviceOperation,
            AttrKey<R> attrKey) {
        return stepContext -> {
            Validation<Violations, C> ctx = safeExtract(requestExtractor, stepContext);

            return ctx.flatMap(c -> {
                Validation<Violations, R> result = serviceOperation.apply(c);
                return result.map(r -> stepContext.withAttribute(attrKey, r));
            });
        };
    }

}
