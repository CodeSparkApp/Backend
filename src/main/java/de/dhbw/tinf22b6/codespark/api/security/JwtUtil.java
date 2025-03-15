package de.dhbw.tinf22b6.codespark.api.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
	private final Environment env;
	private final SecretKey key;

	public JwtUtil(@Autowired Environment env) {
		this.env = env;
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getRequiredProperty("auth.jwt.secret")));
	}

	public String generateAccessToken(UUID accountId, String role) {
		return Jwts.builder()
				.subject(accountId.toString())
				.claim("role", role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + env.getRequiredProperty("auth.jwt.access-token-expiration", Long.class)))
				.signWith(key)
				.compact();
	}

	public String generateRefreshToken(UUID accountId) {
		return Jwts.builder()
				.subject(accountId.toString())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + env.getRequiredProperty("auth.jwt.refresh-token-expiration", Long.class)))
				.signWith(key)
				.compact();
	}

	public UUID extractAccountID(String token) {
		return UUID.fromString(Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject());
	}

	public String extractRole(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}
}
