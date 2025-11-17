package com.sunbooking.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Service for JWT token generation, validation, and parsing.
 * Handles token lifecycle including generation, validation, and extraction of
 * claims.
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private MessageSource messageSource;

    /**
     * Convert hex string secret to byte array for signing.
     * 
     * @return byte array of the secret key
     */
    private byte[] getSigningKey() {
        // Convert hex string to bytes
        int len = jwtSecret.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(jwtSecret.charAt(i), 16) << 4)
                    + Character.digit(jwtSecret.charAt(i + 1), 16));
        }
        logger.debug("JWT Secret length: {} chars, Key size: {} bytes ({} bits)", len, data.length, data.length * 8);
        return data;
    }

    /**
     * Generate JWT token for user.
     *
     * @param userDetails the user details
     * @return JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with custom claims.
     *
     * @param extraClaims additional claims to include
     * @param userDetails the user details
     * @return JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return createToken(extraClaims, userDetails.getUsername());
    }

    /**
     * Create JWT token with claims.
     *
     * @param claims   the claims to include
     * @param username the username (subject)
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, getSigningKey())
                .compact();
    }

    /**
     * Extract username from token.
     *
     * @param token the JWT token
     * @return username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract claim
     * @param <T>            claim type
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token.
     *
     * @param token the JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token.
     *
     * @param token       the JWT token
     * @param userDetails the user details
     * @return true if valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate JWT token with detailed error logging.
     *
     * @param token the JWT token
     * @return true if valid
     * @throws JwtValidationException if token is invalid
     */
    public Boolean validateToken(String token) {
        Locale locale = LocaleContextHolder.getLocale();

        try {
            Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            String errorMsg = messageSource.getMessage("jwt.error.invalid.signature", null, locale);
            throw new JwtValidationException(errorMsg);
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            String errorMsg = messageSource.getMessage("jwt.error.invalid.token", null, locale);
            throw new JwtValidationException(errorMsg);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            String errorMsg = messageSource.getMessage("jwt.error.expired", null, locale);
            throw new JwtValidationException(errorMsg);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            String errorMsg = messageSource.getMessage("jwt.error.unsupported", null, locale);
            throw new JwtValidationException(errorMsg);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty");
            String errorMsg = messageSource.getMessage("jwt.error.empty.claims", null, locale);
            throw new JwtValidationException(errorMsg);
        }
    }

    /**
     * Get token expiration time in milliseconds.
     *
     * @return expiration time
     */
    public Long getExpirationTime() {
        return jwtExpiration;
    }
}
