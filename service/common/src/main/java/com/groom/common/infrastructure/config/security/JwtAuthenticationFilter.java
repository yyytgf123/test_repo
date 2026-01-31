package com.groom.common.infrastructure.config.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	private static final List<String> SKIP_PREFIXES = List.of(
		"/actuator/",
		"/swagger-ui/",
		"/v3/api-docs/",
		"/api/v1/auth/",
		"/api/v1/internal/",
		"/internal/"
	);

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = getPath(request);
		if ("/health".equals(path)) return true;

		// 필요하면 더 추가 가능
		if ("/swagger-ui.html".equals(path)) return true;

		for (String prefix : SKIP_PREFIXES) {
			if (path.startsWith(prefix)) return true;
		}
		return false;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		// ✅ 토큰이 없으면(헬스체크 포함) 그냥 통과
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7).trim();
		if (token.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// ✅ 네 JwtUtil은 실패 시 CustomException 던짐 → 여기서 잡아야 500 안남
			jwtUtil.validateToken(token);

			UUID userId = jwtUtil.getUserIdFromToken(token);
			String role  = jwtUtil.getRoleFromToken(token);

			if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				// role 문자열을 권한으로 변환 (ROLE_ prefix 보정)
				List<GrantedAuthority> authorities = toAuthorities(role);

				// principal은 그냥 userId 문자열로 둬도 됨(간단)
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			// ✅ 여기서 500 나지 않게 401로 종료 (원하면 통과로 바꿔도 됨)
			unauthorized(response);
		}
	}

	private List<GrantedAuthority> toAuthorities(String role) {
		if (role == null || role.isBlank()) return List.of();
		String normalized = role.startsWith("ROLE_") ? role : "ROLE_" + role;
		return List.of(new SimpleGrantedAuthority(normalized));
	}

	private String getPath(HttpServletRequest request) {
		// contextPath 포함 환경 고려
		String uri = request.getRequestURI();
		String ctx = request.getContextPath();
		if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
			return uri.substring(ctx.length());
		}
		return uri;
	}

	private void unauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Unauthorized\"}");
	}
}
