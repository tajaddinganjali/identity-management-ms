package com.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibam.errorhandling.api.ErrorResponseDTO;
import com.ibam.errorhandling.api.RestExceptionHandler;
import com.management.config.security.JwtTokenAuthenticationFilter;
import com.management.exception.AuthErrorEnum;
import com.management.exception.AuthException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestExceptionHandler restExceptionHandler;
    private final ObjectMapper objectMapper;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter,
                          RestExceptionHandler restExceptionHandler, ObjectMapper objectMapper) {
        this.jwtTokenAuthenticationFilter = jwtTokenAuthenticationFilter;
        this.objectMapper = objectMapper;
        this.restExceptionHandler = restExceptionHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // doesn't really make sense to protect a REST API using form login but it is just for illustration
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // handle an authorized attempts
                .exceptionHandling()
                .authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                // Add a filter to validate the tokens with every request
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // authorization requests config
                .authorizeRequests()
                // allow all who are accessing "auth" service
                .antMatchers("/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/actuator/**",
                        "/registration/pin"
                ).permitAll()
                // allow all who are accessing "auth" service
                .anyRequest().hasAuthority("user").and().exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    ResponseEntity<ErrorResponseDTO> errorResponse =
                            restExceptionHandler.handleIBAMException(new AuthException(AuthErrorEnum.UNAUTHORIZED,
                                    "Authentication error. " + authException.getMessage()), request);

                    ErrorResponseDTO errorResponseDTO = errorResponse.getBody();

                    String errorResponseJson = objectMapper.writeValueAsString(errorResponseDTO);

                    response.setContentType("application/json; charset=UTF-8");
                    response.setStatus(errorResponse.getStatusCodeValue());
                    response.getWriter().write(errorResponseJson);
                });
    }

}
