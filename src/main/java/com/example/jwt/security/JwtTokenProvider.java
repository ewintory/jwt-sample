package com.example.jwt.security;

import com.example.jwt.exception.AuthException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final String secretKey = "F)J@McQfTjWnZr4u7x!A%D*G-KaPdSgU";
    private final long validityInMilliseconds = 3600000; // 1h

    private final AppUserDetails appUserDetails;

    public JwtTokenProvider(AppUserDetails appUserDetails) {
        this.appUserDetails = appUserDetails;
    }

    public String createToken(String username, Long userId) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
            .type(JOSEObjectType.JWT)
            .build();

        JWTClaimsSet payload = new JWTClaimsSet.Builder()
            .issuer("jwt-sample")
            .subject(username)
            .claim("userId", userId)
            .issueTime(new Date())
            .expirationTime(Date.from(Instant.now().plusMillis(validityInMilliseconds)))
            .build();

        SignedJWT signedJWT = new SignedJWT(header, payload);
        try {
            signedJWT.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return signedJWT.serialize();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = appUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean isValid = SignedJWT.parse(token)
                .verify(new MACVerifier(secretKey));

            if (!isValid) {
                log.error("Token is invalid");
                throw new AuthException("Token is invalid");
            }

            // return jwt.getJWTClaimsSet().getLongClaim("userId");
            return jwt.getJWTClaimsSet().getSubject();
        } catch (JOSEException | ParseException e) {
            log.error("Failed to verify token", e);
            throw new AuthException("Failed to verify token");
        }
    }

    public String resolveToken(HttpServletRequest req) {
        if (req.getCookies() == null) {
            return null;
        }

        return Arrays.stream(req.getCookies())
            .filter(cookie -> "X-Token".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    public boolean validateToken(String token) {
        try {
            return SignedJWT.parse(token)
                .verify(new MACVerifier(secretKey));
        } catch (JOSEException | ParseException e) {
            log.error("Failed to verify token", e);
            throw new AuthException("Failed to verify token");
        }
    }

}