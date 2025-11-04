package org.example.cm.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 토큰 재발급 요청은 필터 통과
        if (requestURI.equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더나 쿠키에서 토큰 추출
        String token = jwtUtil.extractToken(request);
        if (token == null) {
            token = extractTokenFromCookie(request, "accessToken");
        }

        // 토큰이 있을 때만 검증 수행
        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    // Access Token 확인
                    if (!jwtUtil.isAccessTokne(token)) {
                        sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                                "INVALID_TOKEN_TYPE", "Access 토큰이 아닙니다.");
                        return;
                    }

                    String email = jwtUtil.getEmailFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // 인증 정보(SecurityContext)에 등록
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "INVALID_TOKEN", "토큰이 유효하지 않거나 만료되었습니다.");
                return;
            }
        }

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }

    // 에러 응답 반환
    private void sendErrorResponse(HttpServletResponse response, int status, String code,String message)
        throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"status\": %d, \"code\": \"%s\", \"message\": \"%s\"}", status, code, message
        ));
    }

    // 쿠키에서 accessToken 추출
    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        for (var cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
