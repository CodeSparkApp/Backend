package de.dhbw.tinf22b6.codespark.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public JwtFilter(@Autowired JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain chain) throws ServletException, IOException {
		String token = request.getHeader("Authorization");

		if ((token != null) && (token.startsWith("Bearer "))) {
			token = token.replace("Bearer ", "");
			if (jwtUtil.validateToken(token)) {
				String username = jwtUtil.extractUsername(token);
				String role = jwtUtil.extractRole(token);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
				);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}

		chain.doFilter(request, response);
	}
}
