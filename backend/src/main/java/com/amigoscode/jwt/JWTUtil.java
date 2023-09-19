package com.amigoscode.jwt;

import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JWTUtil {

    /*public String issueToken(String subject, Map<String, Object> claims){
        Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuer("")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
    }*/
}
