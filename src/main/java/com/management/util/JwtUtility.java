package com.management.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.management.exception.AuthErrorEnum;
import com.management.exception.AuthException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.model.Channel;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.util.constant.PropertyConstants;
import com.management.util.constant.TokenConstants;
import com.management.util.dto.JwtUtilityDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
public class JwtUtility implements IJwtUtility {

    @Value("${iam-svc.certificate.private-key}")
    private String path;

    @Value("${iam-svc.jwt.issuer}")
    private String issuer;

    private Key privateKey;

    private static Key loadKey(InputStream in, Function<byte[], Key> keyParser) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String line;

            StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {

                if (!(line.contains("BEGIN") || line.contains("END"))) {

                    content.append(line).append('\n');

                }

            }

            byte[] encoded = Base64.decodeBase64(content.toString());

            return keyParser.apply(encoded);

        }

    }

    public static Key loadPrivateKey(InputStream in) throws IOException, NoSuchAlgorithmException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return loadKey(in, bytes -> {

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

            try {
                return keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                throw new AuthException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, e.getMessage());
            }
        });
    }

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException {

        File file = ResourceUtils.getFile(Objects.requireNonNull(path));
        byte[] keyBytes = Files.readAllBytes(Paths.get(file.getPath()));
        InputStream myInputStream = new ByteArrayInputStream(keyBytes);
        privateKey = loadPrivateKey(myInputStream);
    }

    private Map<String, Object> createClaimsForWeb(String userId, String sessionId, ConsumerTypeEnum consumerTypeEnum) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("uid", userId);
        claims.put("sid", sessionId);
        claims.put("cid", consumerTypeEnum.toString());
        claims.put("azp", consumerTypeEnum.toString());
        claims.put("scope", consumerTypeEnum.toString());
        claims.put("channel", Channel.WEB.toString());

        return claims;
    }

    private Map<String, Object> createClaimsForMobile(JwtUtilityDTO jwtUtility) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("uid", jwtUtility.getUserId());
        claims.put("sid", jwtUtility.getSessionId());
        claims.put("cid", jwtUtility.getConsumerTypeEnum());
        claims.put("did", jwtUtility.getDeviceId());
        claims.put("azp", jwtUtility.getConsumerTypeEnum());
        claims.put("scope", jwtUtility.getConsumerTypeEnum());
        claims.put("channel", Channel.MOBILE.toString());

        return claims;
    }

    @Override
    public String createAccessTokenFoWeb(JwtUtilityDTO jwtUtility) {
        return generateTokenForWeb(jwtUtility.getUserId(), jwtUtility.getSessionId(), jwtUtility.getConsumerTypeEnum(),
                TokenConstants.WEB_ACCESS_TOKEN_EXPIRE_TIME_IN_SECONDS);
    }

    @Override
    public String createAccessTokenForMobile(JwtUtilityDTO jwtUtility) {
        return generateTokenForMobile(jwtUtility,
                TokenConstants.MOBILE_ACCESS_TOKEN_EXPIRE_TIME_SECS);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return decodeJwtAndValidateProperty(token, PropertyConstants.USER_ID_PROPERTY_NAME);
    }

    @Override
    public String getDeviceIdFromToken(String token) {
        return decodeJwtAndValidateProperty(token, PropertyConstants.DEVICE_ID_PROPERTY_NAME);
    }

    @Override
    public String getSessionIdFromToken(String token) {
        return decodeJwtAndValidateProperty(token, PropertyConstants.SESSION_ID_PROPERTY_NAME);
    }

    @Override
    public String getConsumerIdFromToken(String token) {
        return decodeJwtAndValidateProperty(token, PropertyConstants.CONSUMER_ID_PROPERTY_NAME);
    }

    @Override
    public String createRefreshTokenForWeb(String userId, String sessionId, ConsumerTypeEnum consumerTypeEnum) {
        return generateTokenForWeb(userId, sessionId, consumerTypeEnum,
                TokenConstants.WEB_REFRESH_TOKEN_EXPIRE_TIME_IN_SECONDS);
    }

    @Override
    public String createRefreshTokenForMobile(JwtUtilityDTO jwtUtility) {
        return generateTokenForMobile(jwtUtility,
                TokenConstants.MOB_REFRESH_TOKEN_EXPIRE_TIME_SECS);
    }

    private String generateTokenForWeb(String userId, String sessionId, ConsumerTypeEnum consumerTypeEnum,
                                       int expireTimeInSeconds) {
        Date issuedAt = TimeUtil.getCurrentDateTime();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss")).replace("-", "");
        String sub = uuid + now;
        Map<String, Object> claims = createClaimsForWeb(userId, sessionId, consumerTypeEnum);
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        return Jwts.builder()
                .setHeaderParams(headers)
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(sub)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setExpiration(DateUtils.addSeconds(issuedAt, expireTimeInSeconds))
                .setIssuedAt(issuedAt)
                .compact();
    }

    private String generateTokenForMobile(JwtUtilityDTO jwtUtility,
                                          int expireTimeInSeconds) {
        Date issuedAt = TimeUtil.getCurrentDateTime();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss")).replace("-", "");
        String sub = uuid + now;
        Map<String, Object> claims = createClaimsForMobile(jwtUtility);
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        return Jwts.builder()
                .setHeaderParams(headers)
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(sub)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .setExpiration(DateUtils.addSeconds(issuedAt, expireTimeInSeconds))
                .setIssuedAt(issuedAt)
                .compact();
    }

    private DecodedJWT decodeJwt(String token) {
        try {
            return JWT.decode(token);
        } catch (JWTDecodeException e) {
            throw new AuthException(AuthErrorEnum.UNAUTHORIZED, "Jwt decode exception: " + e.getMessage());
        } catch (Exception e) {
            throw new AuthException(AuthErrorEnum.UNAUTHORIZED, e.getMessage());
        }
    }

    private String decodeJwtAndValidateProperty(String token, String property) {
        DecodedJWT decodedJwt = decodeJwt(token);
        Map<String, Claim> claims = decodedJwt.getClaims();
        try {
            return claims.get(property).asString();
        } catch (Exception e) {
            throw new AuthException(AuthErrorEnum.UNAUTHORIZED, property + " is missing in token");
        }
    }

    @Override
    public Boolean isExpired(String token) {
        DecodedJWT decodedJwt = decodeJwt(token);
        return decodedJwt.getExpiresAt().before(TimeUtil.getCurrentDateTime());
    }

}
