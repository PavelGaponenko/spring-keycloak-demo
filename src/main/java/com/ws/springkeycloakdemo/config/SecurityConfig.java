package com.ws.springkeycloakdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.stream.Stream;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .oauth2Login(Customizer.withDefaults()).logout(logout -> logout
                .logoutSuccessUrl("/")
                .logoutUrl("/logout")
                .invalidateHttpSession(true) // Очистка HTTP сессии после logout
                .deleteCookies("JSESSIONID", "SESSION") // Удаление cookies после logout
                .clearAuthentication(true)
            )
            .authorizeHttpRequests(
                customizer -> customizer
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/manager").hasRole("MANAGER")
                    .anyRequest().authenticated()
            )
            .build();
    }

    /**
     * Выбираем значение строки scope, выбираем значения для прав
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // указываем откуда брать имя пользователя
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");

        var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // конвертер для прав
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            var roles = jwt.getClaimAsStringList("demo_roles");

            return Stream.concat(
                authorities.stream(),
                roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast))
                .toList();
        });

        return jwtAuthenticationConverter;
    }

    /**
     * OidcUserRequest - запрос к Keycloak
     * OidcUser - возвращенный
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        var oidcUserService = new OidcUserService();
        return userRequest -> {
            var oidcUser = oidcUserService.loadUser(userRequest);
            var roles = oidcUser.getClaimAsStringList("demo_roles");
            var authorities = Stream.concat(
                oidcUser.getAuthorities().stream(),
                roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                )
                .toList();

            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
}
