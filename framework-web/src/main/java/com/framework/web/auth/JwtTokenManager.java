package com.framework.web.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.framework.common.constants.StringConstant;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;


public class JwtTokenManager {

    public static JwtToken getToken(String subject, String token) {
        DecodedJWT jwt = parseToken(subject, token);
        JwtToken jwtToken = new JwtToken();
        jwtToken.setIssuedAt(jwt.getIssuedAt());
        jwtToken.setExpiresAt(jwt.getExpiresAt());
        jwtToken.setUserId(jwt.getAudience().get(0));
        return jwtToken;
    }

    public static DecodedJWT parseToken(String subject, String token) {
        Algorithm algorithm = Algorithm.HMAC512(subject);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(StringConstant.FRAMEWORK)
                .build();
        return verifier.verify(token);
    }

    public static String generateToken(String userId, String subject, int minutes) {
        try {
            Date date = new Date();
            DateTime dateTime = new DateTime(date);
            Date expiredTime = dateTime.plusMinutes(minutes).toDate();
            Algorithm algorithm = Algorithm.HMAC512(subject);
            return JWT.create()
                    .withIssuer(StringConstant.FRAMEWORK)
                    .withIssuedAt(date)
                    .withExpiresAt(expiredTime)
                    .withJWTId(UUID.randomUUID().toString())
                    .withAudience(userId)
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            return StringConstant.EMPTY;
        }
    }

}