package com.cognixia.jump.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cognixia.jump.filter.JWTRequestFilter;
import com.cognixia.jump.service.MyUserDetailsService;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JWTRequestFilter jwtRequestFilter;
	
	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
		
		// security will only find the one built-in user within the service
		auth.userDetailsService( userDetailsService );
	}
	
	@Override
	protected void configure( HttpSecurity http) throws Exception {
		
		// http://localhost:8080/authenticate --> sent user credentials to create JWT
		
//		http.csrf().disable()
//			.authorizeRequests()
//			.antMatchers("/authenticate").permitAll() // allow anyone to create a JWT as long as they have a username + password
//			.anyRequest().authenticated(); // any other API in this project need to be authenticated (token or user info)
		
		
		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/api/register").permitAll()
			.antMatchers("/api/authenticate").permitAll() // anyone can create token if they're a user
			.antMatchers("/api/change/password").permitAll()
			.antMatchers("/api/user/cart").permitAll()
			.antMatchers("/api/user/cart/{id}").permitAll()
			.antMatchers("/api/user/checkout").permitAll()
			.antMatchers("/api/user/addToCart").permitAll()
			.antMatchers("/api/user/addToCart/{id}").permitAll()
			.antMatchers("/api/user").hasRole("ADMIN")
			.antMatchers("/api/user/{name}").hasRole("ADMIN")
			.antMatchers("/api/user/{id}").hasRole("ADMIN")
			.antMatchers("/api/books/add").hasRole("ADMIN")
			.antMatchers("/api/book/{id}").hasRole("ADMIN")
			.antMatchers("/api/book/inventory").hasRole("ADMIN")
			.antMatchers("/api/books").permitAll()
			.antMatchers("/api/books/{id}").permitAll()
			.antMatchers("/api/cart").hasRole("ADMIN")
			.anyRequest().authenticated() // any other API in this project need to be authenticated (token or user info)
			.and().sessionManagement()
			.sessionCreationPolicy( SessionCreationPolicy.STATELESS ); // tell spring security to NOT CREATE SESSIONS, want to be stateless b/c JWTs

		
		
		// make sure jwt filter checked before any other filter, especially befor the filter that checks for a correct username & password
		
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
	}
}
