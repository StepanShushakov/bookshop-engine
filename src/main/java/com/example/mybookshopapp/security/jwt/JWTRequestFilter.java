package com.example.mybookshopapp.security.jwt;

import com.example.mybookshopapp.security.services.BookstoreUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author karl
 */

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookstoreUserDetailService bookstoreUserDetailService;
    private final JWTUtil jwtUtil;
    private final JWTBlacklistService blacklistService;

    @Autowired
    public JWTRequestFilter(BookstoreUserDetailService bookstoreUserDetailService, JWTUtil jwtUtil, JWTBlacklistService blacklistService) {
        this.bookstoreUserDetailService = bookstoreUserDetailService;
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    try {
                        username = jwtUtil.extractUsername(token);
                    }catch (ExpiredJwtException e) {
                        e.printStackTrace();
                    }
                }

                if (username != null
                        && SecurityContextHolder.getContext().getAuthentication() == null
                        && !blacklistService.tokenAtBlacklist(token)) {
                    UserDetails userDetails = bookstoreUserDetailService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
