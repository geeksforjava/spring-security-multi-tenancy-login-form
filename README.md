# Multi-tenancy Login Form with Spring Security

Spring Security 를 사용하는 경우 Multi-tenancy 환경에서 로그인 페이지를 각 Tenancy 별로 구성하는 방법을 설명하는 코드를 제공합니다.


## 언제 사용하나요?

Spring Security 를 사용하면 권한이 없는 페이지에 접근 시 지정된 로그인 페이지로 이동하게 됩니다. 
하지만 Multi-tenancy 환경에서는 각 조직 (Tenancy) 마다 로그인 페이지 디자인을 다르게 구성하고 싶을 수 있습니다.

## 구성 정보

- JDK 17
- Spring Boot 3
- Thymeleaf
- Tailwindcss

## 기본 개념

`LoginUrlAuthenticationEntryPoint` 을 확장하는 방법을 사용합니다. 이 클래스의 `determineUrlToUseForThisRequest()` 메소드를 오버라이드해서 특정 상황에 맞는 Login Page 주소를 반환하게 할 수 있습니다.

## 가이드

쉬운 테스트 환경 구성을 위해서 Multi-tenancy 는 Path 기반으로 정의합니다. 아래와 같은 패턴을 사용합니다. 첫 번재 Path 를 Tenant ID 로 사용합니다.

```
http://localhost:8080/{tenantId}/index
```

이 가이드에서는 삼성과 엘지를 예시로 구성했습니다. 아래와 같이 호출해서 테스트합니다.

- `http://localhost:8080/samsung/index`
- `http://localhost:8080/lg/index`

### TenantLoginUrlAutehtnicationEntryPoint

아래와 같이 첫 번째 경로 (Tenant ID) 를 추출해서 지정된 Login Form URL 을 반환합니다.

```java
public class TenantLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    ...
    
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {
        log.debug("Determining multi-tenancy login url...");

        String tenantId = TenantUtils.resolveTenantId(request);
        log.trace("Resolved tenant identifier => {}", tenantId);

        return String.format("/%s/login", tenantId);
    }

    ...
}
```

이제 위에서 반환한 URL 에 대응하는 Controller 를 작성합니다.

### TenantLoginController

정말 간단합니다. 미리 만들어진 Login Form 화면을 호출하는 코드만 작성하면 됩니다.

```java
@Controller
@RequestMapping("/{tenantId}/login")
public class TenantLoginController {

    @GetMapping
    public String loginPage(@PathVariable String tenantId, Model model) {

        return String.format("login/%s", tenantId);
    }
}
```

### SecurityConfig

이제 Spring Security 를 구성합니다.

```java
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/*/login", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor( 
                        tenantLoginUrlAuthenticationEntryPoint(),
                        new AntPathRequestMatcher("/*/**")
                ));

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint tenantLoginUrlAuthenticationEntryPoint(){
        return new TenantLoginUrlAuthenticationEntryPoint("/login");
    }

}
```