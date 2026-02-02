package org.dotspace.oofp.utils.eip.auth;

import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.utils.eip.inbound.AuthContextExtractors;
import org.dotspace.oofp.utils.functional.monad.Maybe;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

@UtilityClass
public class Authentications {

    public Validation<Violations, Authentication> getAuthentication() {
        var auth = Maybe.given(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .orElse(null);

        if (auth == null) {
            return Validation.invalid(
                    Violations.violate("auth.missing", "無法取得 Authentication 資訊")
            );
        }

        if (auth instanceof AnonymousAuthenticationToken) {
            return Validation.invalid(
                    Violations.violate("auth.anonymous", "目前為匿名使用者")
            );
        }

        return Validation.valid(auth);
    }

    public <T> T getPrincipal(Class<T> principalClazz) {
        return getAuthentication().get()
                .map(Authentication::getPrincipal)
                .filter(principalClazz::isInstance)
                .map(principalClazz::cast)
                .orElse(null);
    }

    public String getPrincipalId() {
       return getAuthentication().get()
                .map(AuthContextExtractors::principalId)
                .orElse(null);
    }

    public Set<String> getAuthorities() {
        return getAuthentication().get()
                .map(AuthContextExtractors::authorities)
                .orElse(Set.of());
    }

    public Set<String> getRolesFromAuthorities() {
        return getAuthentication().get()
                .map(AuthContextExtractors::authorities)
                .map(AuthContextExtractors::rolesFromAuthorities)
                .orElse(Set.of());
    }

    public Set<String> getScopesFromAuthorities() {
        return getAuthentication().get()
                .map(AuthContextExtractors::authorities)
                .map(AuthContextExtractors::scopesFromAuthorities)
                .orElse(Set.of());
    }

    public Set<String> getRoleGroupsFromAuthorities() {
        return getAuthentication().get()
                .map(AuthContextExtractors::authorities)
                .map(AuthContextExtractors::scopesFromAuthorities)
                .orElse(Set.of());
    }

}
