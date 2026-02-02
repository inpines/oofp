package org.dotspace.oofp.utils.eip.inbound;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
public class AuthContextExtractors {

    // 取得 principal id
    public String principalId(Authentication auth) {
        Object p = auth.getPrincipal();
        if (p instanceof UserDetails ud) {
            return ud.getUsername();
        }

        return auth.getName();
    }

    // 取得 authorities 清單
    public Set<String> authorities(Authentication auth) {
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toUnmodifiableSet());
    }

    public Set<String> rolesFromAuthorities(Authentication auth) {
        return rolesFromAuthorities(authorities(auth));
    }

    // 從 authorities 解析出 roles
    public Set<String> rolesFromAuthorities(Set<String> authorities) {
        return authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> scopesFromAuthorities(Authentication auth) {
        return scopesFromAuthorities(authorities(auth));
    }

    // 從 authorities 解析出 scopes
    public Set<String> scopesFromAuthorities(Set<String> authorities) {
        return authorities.stream()
                .filter(a -> a.startsWith("SCOPE_"))
                .map(a -> a.substring("SCOPE_".length()))
                .collect(Collectors.toUnmodifiableSet());
    }

}
