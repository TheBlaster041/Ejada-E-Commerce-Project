package com.example.wallet.security;
import com.example.common.security.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;

@Configuration
public class SecurityConfig
{
	@Bean
	JwtUtil jwtUtil()
	{
		return new JwtUtil();
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http,JwtUtil jwt)
			throws Exception 
			{
				return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(a->a.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated())
				.addFilterBefore(new JwtAuthFilter(jwt), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class).build();
			}
}
