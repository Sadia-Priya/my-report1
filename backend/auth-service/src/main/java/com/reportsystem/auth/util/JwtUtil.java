package com.reportsystem.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // application.yaml er jwt.secret
    @Value("${jwt.secret}")
    private String secret;

    // User er jonno login token banay. Token 1 din cholbe.
    public String createToken(String email) {
        long oneDay = 24 * 60 * 60 * 1000;

        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + oneDay))
                .sign(Algorithm.HMAC256(secret));
    }
}