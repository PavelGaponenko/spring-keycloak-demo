# Описание проекта

## Keycloak

### Начало работы

Ссылка на Keycloak: http://localhost:18080/

Логин/пароль: (см. KEYCLOAK_ADMIN)

Подготовительная работа с Keycloak:

- создать realm
- создать пользователя
- задать пароль пользователя
- создать клиента приложения

При создании клиента приложения:

- client authentication нужен, чтобы запрашивался client secret,
- root url – адрес приложения (http://localhost:8081)
- valid redirect – валидный адрес перенаправления `http://localhost:8081/*`

> Вкладка credentials – можно посмотреть client secret для клиента

### Проверка аутентификации

Получение токена:

```http request
POST http://localhost:18080/realms/ws-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=spring-keycloak-demo&client_secret=7LAn5Rp8olsD1Jn2y6t82P79Ts3jKAwG&username=p.gaponenko&password=p.gaponenko&grant_type=password
```

## Ресурс-сервер

Для создания ресурс-сервера необходимо добавить конфигурацию Spring Security:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(Customizer.withDefaults())
        )
        .authorizeHttpRequests(
            customizer -> customizer
                .requestMatchers("/manager").hasRole("MANAGER")
                .anyRequest().authenticated()
        )
        .build();
}
```

После получения токена можно проверить работу ресурс-сервера, обратившись к одному из методов:

```http request
GET http://localhost:8081/authenticated
Authorization: Bearer ...
```

## Роли Keycloak

- создаем роль: `ROLE_MANAGER`
- привязываем роль к пользователю (в разделе пользователя)

Для получения ролей в приложении необходимо реализовать конвертер токена:

```java
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    var converter = new JwtAuthenticationConverter();
    converter.setPrincipalClaimName("preferred_username");

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        var roles = jwt.getClaimAsStringList("app_roles");

        return roles.stream()
            .filter(role -> role.startsWith("ROLE_"))
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .toList();
    });

    return converter;
}
```

- `preferred_username` – имя пользователя
- `app_roles` – кастомное свойство со списком ролей

Для переопределения необходимо задать маппер.

В Keycloak переходим в `client scopes` -> `roles` -> `mappers` -> `by configuration` -> `user realm role`

## OAuth Login (Клиент)

В цепочки фильтров безопасности добавляем (для входа и выхода:

```
.oauth2Login(Customizer.withDefaults())
.logout((logout) -> {
    var handler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    handler.setPostLogoutRedirectUri("{baseUrl}/");
    logout.logoutSuccessHandler(handler);
})
```

Реализуем UserService для получения ролей клиентом:

```java
@Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        var oidcUserService = new OidcUserService();
        return userRequest -> {
            var oidcUser = oidcUserService.loadUser(userRequest);
            var roles = oidcUser.getClaimAsStringList("app_roles");

            var authorities = roles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
```

## Работа с приложением

Приложение доступно по ссылке: http://localhost:8081/
