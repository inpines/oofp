package org.dotspace.oofp.utils.eip.auth;

import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class EntitlementsResolvers {

    public EntitlementsResolver denyAll() {
        return pid -> Validation.invalid(Violations.violate(
                "auth-binding.entitlements.deny", "FORBIDDEN"));
    }

    public EntitlementsResolver readOnly(Set<String> roleNames, Set<String> authorityNames) {
        return pid -> Validation.valid(
                new EntitlementsResolver.Entitlements(Set.copyOf(roleNames), Set.of(), Set.copyOf(authorityNames))
        );
    }

    public EntitlementsResolver fixed(
            Set<String> roleNames,
            Set<String> roleGroups,
            Set<String> authorityNames) {

        Set<String> roles = Set.copyOf(roleNames);
        Set<String> groups = Set.copyOf(roleGroups);
        Set<String> authorities = Set.copyOf(authorityNames);

        return pid -> Validation.valid(
                new EntitlementsResolver.Entitlements(
                        roles,
                        groups,
                        authorities
                )
        );

    }

    /**
     * 從外部來源讀取 Entitlements
     *
     * @param sourceReader 真正的資料來源（DB / API / cache / mock）
     */
    public EntitlementsResolver readFrom(EntitlementsResolver sourceReader) {
        return sourceReader;
    }

}
