package de.dhbw.tinf22b6.codespark.api.security;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
	private final JwtUtil jwtUtil;
	private final AccountRepository accountRepository;

	@Autowired
	public WebSocketAuthInterceptor(@Autowired JwtUtil jwtUtil,
									@Autowired AccountRepository accountRepository) {
		this.jwtUtil = jwtUtil;
		this.accountRepository = accountRepository;
	}

	@Override
	public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if ((accessor == null) || (accessor.getCommand() != StompCommand.CONNECT)) {
			return message;
		}

		String authorizationValue = accessor.getFirstNativeHeader("Authorization");
		if ((authorizationValue == null) || (!authorizationValue.startsWith("Bearer "))) {
			return message;
		}

		String token = authorizationValue.replace("Bearer ", "");
		if (jwtUtil.validateToken(token)) {
			String username = jwtUtil.extractUsername(token);
			String role = jwtUtil.extractRole(token);

			Account account = accountRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("Username not found"));

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					account.getUsername(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
			);

			SecurityContextHolder.getContext().setAuthentication(authToken);
			accessor.setUser(authToken);
		}

		return message;
	}
}
