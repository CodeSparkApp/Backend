package de.dhbw.tinf22b6.codespark.api.security;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
	private final AccountRepository accountRepository;
	private final JwtUtil jwtUtil;

	public JwtFilter(@Lazy AccountRepository accountRepository,
					 @Autowired JwtUtil jwtUtil) {
		this.accountRepository = accountRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain chain) throws ServletException, IOException {
		String authorizationValue = request.getHeader("Authorization");
		if ((authorizationValue == null) || !authorizationValue.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		String token = authorizationValue.replace("Bearer ", "");
		if (jwtUtil.validateToken(token)) {
			String username = jwtUtil.extractUsername(token);
			String role = jwtUtil.extractRole(token);

			Account account = accountRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("Username not found"));

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					// Prefix with 'ROLE_' so 'hasRole()' can be used instead of 'hasAuthority()'
					account, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
			);

			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authToken);
			SecurityContextHolder.setContext(context);

			// Explicitly save the SecurityContext for async requests
			securityContextRepository.saveContext(context, request, response);
		}

		chain.doFilter(request, response);
	}
}
