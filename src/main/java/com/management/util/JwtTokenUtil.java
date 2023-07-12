package com.management.util;

import com.management.identity.enums.ProfileType;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.util.constant.PropertyConstants;
import com.management.util.constant.TokenConstants;
import com.management.util.dto.JwtUtilityDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

@Component
public final class JwtTokenUtil implements Serializable {

    private static final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
    private static final long serialVersionUID = -3301605591108950415L;
    private static final String JWT_PREFIX = "Bearer ";
    private static final String EMPTY_UUID_STR = "00000000-0000-0000-0000-000000000000";
    private static final String JWT_SECRET = "wteueirpkrr.rjbdjndjnr-dbjdndjr";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    private JwtTokenUtil() {

    }

    public static JwtTokenUtil getInstance() {
        return jwtTokenUtil;
    }

    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSessionIdFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.SESSION_ID_PROPERTY_NAME)) {
            return allClaims.get(PropertyConstants.SESSION_ID_PROPERTY_NAME).toString();
        } else {
            return UUID.fromString(EMPTY_UUID_STR).toString();
        }
    }

    public ProfileType getProfileTypeFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.PROFILE_TYPE)
                && Objects.nonNull(allClaims.get(PropertyConstants.PROFILE_TYPE))) {
            if (allClaims.get(PropertyConstants.PROFILE_TYPE).toString().equals(ProfileType.CUSTOMER.toString())) {
                return ProfileType.CUSTOMER;
            } else if (allClaims.get(PropertyConstants.PROFILE_TYPE).toString()
                    .equals(ProfileType.POTENTIAL_CUSTOMER.toString())) {
                return ProfileType.POTENTIAL_CUSTOMER;
            } else {
                return ProfileType.CUSTOMER;
            }
        } else {
            return ProfileType.CUSTOMER;
        }
    }

    public String getConsumerIdFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.CONSUMER_ID_PROPERTY_NAME)) {
            return allClaims.get(PropertyConstants.CONSUMER_ID_PROPERTY_NAME).toString();
        } else {
            return "";
        }
    }

    public String getDeviceIdFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.DEVICE_ID_PROPERTY_NAME)) {
            return allClaims.get(PropertyConstants.DEVICE_ID_PROPERTY_NAME).toString();
        } else {
            return "";
        }
    }

    public String getPhoneFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.PHONE_PROPERTY_NAME)) {
            return allClaims.get(PropertyConstants.PHONE_PROPERTY_NAME).toString();
        } else {
            return "";
        }
    }

    public String getTokenTypeFromToken(String token) {
        Claims allClaims = getAllClaimsFromToken(token);
        if (allClaims.containsKey(PropertyConstants.TOKEN_TYPE_PROPERTY_NAME)) {
            return allClaims.get(PropertyConstants.TOKEN_TYPE_PROPERTY_NAME).toString();
        } else {
            return "";
        }
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JWT_SECRET)).parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(JwtUtilityDTO jwtUtility) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(PropertyConstants.SESSION_ID_PROPERTY_NAME, jwtUtility.getSessionId());
        claims.put(PropertyConstants.CONSUMER_ID_PROPERTY_NAME, jwtUtility.getConsumerTypeEnum().getConsumerId());
        claims.put(PropertyConstants.TOKEN_TYPE_PROPERTY_NAME, jwtUtility.getTokenType());
        claims.put(PropertyConstants.PROFILE_TYPE, jwtUtility.getProfileType());

        if (jwtUtility.getConsumerTypeEnum() == ConsumerTypeEnum.MOBILE) {
            if (StringUtils.isNotBlank(jwtUtility.getUserId())) {
                claims.put(PropertyConstants.USER_ID_PROPERTY_NAME, jwtUtility.getUserId());
            }
            if (StringUtils.isNotBlank(jwtUtility.getPhone())) {
                claims.put(PropertyConstants.PHONE_PROPERTY_NAME, jwtUtility.getPhone());
            }
            claims.put(PropertyConstants.DEVICE_ID_PROPERTY_NAME, jwtUtility.getDeviceId());
        }
        return doGenerateToken(claims, jwtUtility.getConsumerTypeEnum(), jwtUtility.getSubject());
    }

    private String doGenerateToken(Map<String, Object> claims, ConsumerTypeEnum consumerTypeEnum, String subject) {
        final Date createdDate = TimeUtil.getCurrentDateTime();
        final Date expirationDate = calculateExpirationDate(createdDate, consumerTypeEnum);

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(createdDate)
                .setExpiration(expirationDate).signWith(signatureAlgorithm, generateTokenKey()).compact();
    }

    private Key generateTokenKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWT_SECRET);

        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    private Date calculateExpirationDate(Date createdDate, ConsumerTypeEnum consumerId) {

        if (consumerId == ConsumerTypeEnum.MOBILE) {
            return DateUtils.addSeconds(createdDate, TokenConstants.MOBILE_ACCESS_TOKEN_EXPIRE_TIME_SECS);
        }
        return DateUtils.addSeconds(createdDate, TokenConstants.WEB_ACCESS_TOKEN_EXPIRE_TIME_IN_SECONDS);
    }

    public String getTokenFromHeader(String header) {
        return header.replace(JWT_PREFIX, "");
    }

    public boolean checkHeader(String header) {
        return header == null || !header.startsWith(JWT_PREFIX);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(TimeUtil.getCurrentDateTime());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


}
