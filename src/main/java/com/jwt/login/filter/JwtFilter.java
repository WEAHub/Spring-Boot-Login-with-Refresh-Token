package com.jwt.login.filter;

import com.jwt.login.service.JwtService;
import com.jwt.login.service.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserInfoService userInfoService;
  @Override
  protected void doFilterInternal(
    HttpServletRequest request, 
    HttpServletResponse response, 
    FilterChain filterChain
  ) throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token= null;
    String userName = null;

    if(authHeader != null){
      token = authHeader.substring(7);
      userName = jwtService.extractUserName(token);

      Boolean hasAuthContext = SecurityContextHolder.getContext().getAuthentication() != null;

      if(!hasAuthContext) {

        UserDetails userDetails = userInfoService.loadUserByUsername(userName);

        if(jwtService.validateToken(token, userDetails)) {

          UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
            );

          authToken.setDetails(
            new WebAuthenticationDetailsSource()
              .buildDetails(request)
          );
          
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
