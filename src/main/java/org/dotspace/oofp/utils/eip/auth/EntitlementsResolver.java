package org.dotspace.oofp.utils.eip.auth;

import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;

import java.util.Set;

@FunctionalInterface
public interface EntitlementsResolver {
//    // 回傳使用者的角色、角色群組與權限
    record Entitlements(Set<String> roles, Set<String> roleGroups, Set<String> authorities) {}

    // 根據 principalId 解析出 Entitlements
    Validation<Violations, Entitlements> resolve(String principalId);
}

