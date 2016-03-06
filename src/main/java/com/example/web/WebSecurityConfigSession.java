package com.example.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.example.web.login.LoginService;
import com.example.web.login.RestAuthenticationEntryPoint;
import com.example.web.login.RestLoginFailureHandler;
import com.example.web.login.RestLoginSuccessHandler;
import com.example.web.login.RestLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigSession extends WebSecurityConfigurerAdapter {
    @Autowired
    private LoginService loginService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
 
    @Autowired
    private RestLoginSuccessHandler restLoginSuccessHandler;

    @Autowired
    private RestLogoutSuccessHandler restLogoutSuccessHandler;

    @Autowired
    private RestLoginFailureHandler restLoginFailureHandler;

    @Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
			.authorizeRequests()
				.antMatchers("/api/**").hasRole("USER")
				.anyRequest().authenticated().and()
			.requiresChannel().anyRequest().requiresSecure().and()
			.formLogin()
				.loginProcessingUrl("/auth/login")
				.successHandler(restLoginSuccessHandler)
				.failureHandler(restLoginFailureHandler)
				.permitAll().and()
			.logout()
				.logoutUrl("/auth/logout")
				.logoutSuccessHandler(restLogoutSuccessHandler)
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll();
		http.portMapper().http(8080).mapsTo(8443);
	}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService);
    }   
}
