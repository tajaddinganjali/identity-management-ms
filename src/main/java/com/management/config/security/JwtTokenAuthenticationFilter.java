package com.management.config.security;

import com.management.config.security.dto.DoFilterRequisiteDTO;
import com.management.exception.AuthErrorEnum;
import com.management.exception.AuthException;
import com.management.identity.enums.ProfileType;
import com.management.util.JwtTokenUtil;
import com.management.util.constant.PropertyConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String DEFAULT_LANG = "az";
    private static final String EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000").toString();
    private final JwtTokenUtil jwtTokenUtil = JwtTokenUtil.getInstance();

    private boolean requiresAuthentication(String requestUri) {
        List<String> publicUrls = new ArrayList<>(Arrays.asList("/v1/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/actuator/**",
                "/registration/pin"
        ));

        Optional<String> publicUrl = publicUrls.stream().filter(requestUri::contains).findFirst();

        return publicUrl.isEmpty();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestId = request.getHeader(PropertyConstants.REQUEST_ID_HEADER_NAME);
        String httpLang = request.getHeader(PropertyConstants.LANG_HEADER_NAME);

        String lang;

        lang = getLang(httpLang);

        if (StringUtils.isEmpty(requestId)) {
            requestId = EMPTY_UUID;
        }

        String apiKey = request.getHeader(PropertyConstants.API_KEY_HEADER_NAME);
        ThreadContext.put(PropertyConstants.REQUEST_ID_PROPERTY_NAME, requestId);
        ThreadContext.put(PropertyConstants.LANG_PROPERTY_NAME, lang);
        ThreadContext.put(PropertyConstants.API_KEY_PROPERTY_NAME, apiKey);

        String header = request.getHeader(TOKEN_HEADER);

        if (requiresAuthentication(request.getRequestURI())) {

            if (jwtTokenUtil.checkHeader(header)) {

                ThreadContext.put(PropertyConstants.USER_ID_PROPERTY_NAME, EMPTY_UUID);
                ThreadContext.put(PropertyConstants.SESSION_ID_PROPERTY_NAME, EMPTY_UUID);

                chain.doFilter(request, response);
            } else {

                String token = jwtTokenUtil.getTokenFromHeader(header);

                try {
                    if (jwtTokenUtil.validateToken(token)) {

                        String userId = jwtTokenUtil.getUserIdFromToken(token);
                        String sessionId = jwtTokenUtil.getSessionIdFromToken(token);
                        String consumerId = jwtTokenUtil.getConsumerIdFromToken(token);
                        String deviceId = jwtTokenUtil.getDeviceIdFromToken(token);
                        String tokenType = jwtTokenUtil.getTokenTypeFromToken(token);

                        ProfileType profileType = jwtTokenUtil.getProfileTypeFromToken(token);

                        ThreadContext.put(PropertyConstants.USER_ID_PROPERTY_NAME, userId);
                        ThreadContext.put(PropertyConstants.SESSION_ID_PROPERTY_NAME, sessionId);

                        DoFilterRequisiteDTO doFilterRequisite = DoFilterRequisiteDTO.builder()
                                .request(request).chain(chain).response(response).token(token).userId(userId)
                                .sessionId(sessionId).consumerId(consumerId).deviceId(deviceId).tokenType(tokenType)
                                .profileType(profileType).build();

                        handleUser(doFilterRequisite);
                    } else {
                        throw new AuthException(AuthErrorEnum.UNAUTHORIZED, "Token expired.");
                    }
                } catch (Exception e) {
                    throw new AuthException(AuthErrorEnum.UNAUTHORIZED, e.getMessage(), e);
                }
            }

        } else {

            ThreadContext.put(PropertyConstants.USER_ID_PROPERTY_NAME, EMPTY_UUID);
            ThreadContext.put(PropertyConstants.SESSION_ID_PROPERTY_NAME, EMPTY_UUID);

            chain.doFilter(request, response);
        }
    }

    private void handleUser(DoFilterRequisiteDTO doFilterRequisite) throws IOException, ServletException {
        if (doFilterRequisite.getUserId() != null) {
            UserPrincipial userPrincipial = UserPrincipial.builder()
                    .userId(doFilterRequisite.getUserId())
                    .sessionId(doFilterRequisite.getSessionId())
                    .consumerId(doFilterRequisite.getConsumerId())
                    .deviceId(doFilterRequisite.getDeviceId())
                    .profileType(doFilterRequisite.getProfileType())
                    .tokenType(doFilterRequisite.getTokenType())
                    .build();
            List<String> roles = List.of("user");
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userPrincipial, doFilterRequisite.getToken(), authorities);


            SecurityContextHolder.getContext().setAuthentication(auth);

            doFilterRequisite.getChain().doFilter(doFilterRequisite.getRequest(), doFilterRequisite.getResponse());
        } else {
            throw new AuthException(AuthErrorEnum.UNAUTHORIZED, "Invalid token.");
        }
    }

    private String getLang(String httpLang) {
        String lang;
        if (!StringUtils.isEmpty(httpLang)) {
            lang = httpLang;
        } else {
            lang = DEFAULT_LANG;
        }
        return lang;
    }

}
