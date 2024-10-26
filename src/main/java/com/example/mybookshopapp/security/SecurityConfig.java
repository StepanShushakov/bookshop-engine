package com.example.mybookshopapp.security;

import com.example.mybookshopapp.security.jwt.JWTRequestFilter;
import com.example.mybookshopapp.security.services.BookstoreUserDetailService;
import com.example.mybookshopapp.security.services.CustomerLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author karl
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final BookstoreUserDetailService bookstoreUserDetailService;
    private final JWTRequestFilter filter;
    private final CustomerLogoutHandler logoutHandler;

    @Autowired
    public SecurityConfig(BookstoreUserDetailService bookstoreUserDetailService,
                          JWTRequestFilter filter,
                          CustomerLogoutHandler logoutHandler) {
        this.bookstoreUserDetailService = bookstoreUserDetailService;
        this.filter = filter;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/my","/profile", "/viewed", "/myarchive").authenticated()//.hasRole("USER")
                .antMatchers("/admin").hasAnyAuthority("ROLE_ADMIN")
                .antMatchers( "/oauth/**", "/**").permitAll()
                .and().formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/signin")
                .addLogoutHandler(logoutHandler)
                .deleteCookies("token")
                        .and()
                        .oauth2Login()
                            .loginPage("/login")
                            .userInfoEndpoint()
                            .and()
                                .successHandler(new AuthenticationSuccessHandler() {
                                    @Override
                                    public void onAuthenticationSuccess(HttpServletRequest request,
                                                                        HttpServletResponse response,
                                                                        Authentication authentication) throws IOException, ServletException {

                                        DefaultOidcUser oauthUser = (DefaultOidcUser) authentication.getPrincipal();
                                        bookstoreUserDetailService.processOAuthPostLogin(oauthUser);

                                        response.sendRedirect("/my");
                                    }
                                })
                        .and().oauth2Client();

        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
