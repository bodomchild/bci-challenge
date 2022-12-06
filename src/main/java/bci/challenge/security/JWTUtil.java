package bci.challenge.security;

import bci.challenge.exception.BciException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret:secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private int expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(String email) throws BciException {
        try {
            Date iat = new Date();
            Date exp = new Date(iat.getTime() + expiration);
            return JWT.create()
                    .withSubject(email)
                    .withIssuer(issuer)
                    .withIssuedAt(iat)
                    .withExpiresAt(exp)
                    .sign(Algorithm.HMAC256(secret));
        } catch (IllegalArgumentException | JWTCreationException e) {
            throw new BciException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating token");
        }
    }

    public String getEmailFromToken(String token) throws BciException {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (IllegalArgumentException | JWTVerificationException e) {
            throw new BciException(HttpStatus.UNAUTHORIZED.value(), "Token not valid");
        }
    }

}
