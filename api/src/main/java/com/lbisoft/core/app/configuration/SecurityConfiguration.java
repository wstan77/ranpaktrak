package com.lbisoft.core.app.configuration;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.lbisoft.core.app.components.btpplatform.service.BtpPlatformService;
import com.lbisoft.core.app.components.security.CompleteAuthProvider;
import com.lbisoft.core.app.components.user.service.AppUserDetailsService;
import com.lbisoft.core.app.settings.AuthenticationSettings;
import com.lbisoft.core.app.settings.SugarCRMSettings;
import com.lbisoft.core.framework.auth.JwtTokenUtil;
import com.lbisoft.core.framework.auth.JwtUserAuthenticationProvider;
import com.lbisoft.core.framework.auth.MultiIssuerJwtDecoder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@EnableMethodSecurity(jsr250Enabled = true)
@OpenAPIDefinition(info = @Info(title = "LBi API", version = "v1"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Configuration
@AutoConfigureAfter()
public class SecurityConfiguration {
    @Autowired
    private AuthenticationSettings authSettings;
    @Autowired
    private CompleteAuthProvider completeAuthProviders;
    @Autowired
    private SugarCRMSettings sugarCRMSettings;

    @Bean
    public JwtUserAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtUserAuthenticationProvider(userDetailsService(), theBtpPlatformService());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT is stateless, don't create session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Return 401 Error if unauthorized
                .exceptionHandling(eh -> eh.authenticationEntryPoint(forbiddenEntryPoint()))
                // JWT Authentication
                // .addFilterBefore(jwtAuthTokenFilterBean(),
                // AnonymousAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> {
                        String adAuthority = "https://login.microsoftonline.com/" + authSettings.getAzureTenantId()
                                + "/v2.0";

                        String adIssuer = "https://sts.windows.net/" + authSettings.getAzureTenantId() + "/";
                        String adClientId = authSettings.getAzureClientId();

                        OAuth2TokenValidator<Jwt> adJwtValidator = new DelegatingOAuth2TokenValidator<>(
                                JwtValidators.createDefaultWithIssuer(adIssuer),
                                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                                        aud -> aud != null && aud.contains(adClientId)));

                        NimbusJwtDecoder adJwtDecoder = NimbusJwtDecoder.withIssuerLocation(adAuthority).build();
                        adJwtDecoder.setJwtValidator(adJwtValidator);

                        String openIdIssuer = authSettings.getOpenIdIssuerUrl();
                        String openIdClientId = authSettings.getOpenIdClientId();

                        OAuth2TokenValidator<Jwt> openIdJwtValidator = new DelegatingOAuth2TokenValidator<>(
                                JwtValidators.createDefaultWithIssuer(openIdIssuer),
                                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                                        aud -> aud != null && aud.contains(openIdClientId)));

                        NimbusJwtDecoder openIdJwtDecoder = NimbusJwtDecoder.withIssuerLocation(openIdIssuer).build();
                        openIdJwtDecoder.setJwtValidator(openIdJwtValidator);

                        MultiIssuerJwtDecoder multiIssuerJwtDecoder = new MultiIssuerJwtDecoder().withIssuer(adIssuer,
                                adJwtDecoder).withIssuer(openIdIssuer, openIdJwtDecoder);

                        try {
                            OAuth2TokenValidator<Jwt> anonJwtValidator = JwtValidators
                                    .createDefaultWithIssuer(authSettings.getAnonymousIssuer());
                            SecretKey secretKey = jwtTokenUtil().getSecretKey();
                            NimbusJwtDecoder anonJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                                    .macAlgorithm(MacAlgorithm.HS512).build();
                            anonJwtDecoder.setJwtValidator(anonJwtValidator);

                            multiIssuerJwtDecoder.withIssuer(authSettings.getAnonymousIssuer(), anonJwtDecoder);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        jwt.decoder(multiIssuerJwtDecoder);
                        jwt.jwtAuthenticationConverter(jwtAuthenticationProvider());
                    });
                })
                .authorizeHttpRequests(authorize -> {
                    // Public End-points
                    authorize.requestMatchers(HttpMethod.POST, "/api/auth/*").permitAll()
                            .requestMatchers(HttpMethod.GET, "/public/**",
                                    "/v3/**", "/swagger-ui/**", "/swagger-ui.html",
                                    "/api/cache/refresh", "/api/build-info/", "/api/health/", "/actuator/health",
                                    "/api/persons/*/headshot")
                            .permitAll()
                            // Allow CORS preflight
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
                            // Require authentication everywhere else
                            .anyRequest().authenticated();
                })
                .headers(headers -> headers.frameOptions(fo -> fo.disable()))
                .httpBasic(basicAuth -> basicAuth.disable())
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() throws NoSuchAlgorithmException {
        return new JwtTokenUtil("IF0K!FnQSzqOEvqOQBP3XFLfKLcZ$czlTlUIhcsA0zPaPz9TOc9X9sR7hHphSR!Z");
    }

    @Bean
    public HttpStatusEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        // successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }

    @Bean
    public AppUserDetailsService userDetailsService() {
        return new AppUserDetailsService();
    }

    @Bean
    public BtpPlatformService theBtpPlatformService() {
        return new BtpPlatformService();
    }

    // @Autowired
    // protected void configureGlobal(AuthenticationManagerBuilder auth) throws
    // Exception {
    // auth.authenticationProvider(completeAuthProviders);
    // }

    @Bean
    AuthenticationManager authenticationManager() {
        return authentication -> completeAuthProviders.authenticate(authentication);
    }
}
