package com.career.plan.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null) {
                logger.debug("=== JWT Filter Debug ===");
                logger.debug("JWT: " + jwt.substring(0, 20) + "...");
                logger.debug("Request URI: " + request.getRequestURI());
                
                if (jwtTokenProvider.validateToken(jwt)) {
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                    List<String> roles = jwtTokenProvider.getRolesFromToken(jwt);
                    
                    logger.debug("Username: " + username);
                    logger.debug("UserId: " + userId);
                    logger.debug("Roles: " + roles);
                    
                    // 设置权限（不加 ROLE_前缀，匹配 hasAnyAuthority() 注解）
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());
                    
                    logger.debug("Authorities: " + authorities);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // 将 userId 和 userRole 放在 request attribute 中供 Controller 使用
                    if (userId != null) {
                        request.setAttribute("userId", userId);
                    }
                    // 设置用户角色（取第一个角色）
                    if (roles != null && !roles.isEmpty()) {
                        request.setAttribute("userRole", roles.get(0));
                    }
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authentication set: " + authentication.getAuthorities());
                } else {
                    logger.debug("Token validation failed");
                }
            } else {
                logger.debug("No JWT found in request");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
