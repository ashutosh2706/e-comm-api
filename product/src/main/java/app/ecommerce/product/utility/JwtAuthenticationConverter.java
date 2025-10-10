package app.ecommerce.product.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(grantedAuthoritiesConverter.convert(source).stream(), extractRoles(source).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        Map<String, Object> resource_access = jwt.getClaim("resource_access");
        if(resource_access != null && resource_access.containsKey("ecomm-api-auth")) {
            Map<String, Object> client_roles = (Map<String, Object>) resource_access.get("ecomm-api-auth");
            if(client_roles != null && client_roles.containsKey("roles"))
                roles.addAll((Collection<? extends String>) client_roles.get("roles"));
        }

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.trim().toUpperCase())).collect(Collectors.toSet());
    }
}
