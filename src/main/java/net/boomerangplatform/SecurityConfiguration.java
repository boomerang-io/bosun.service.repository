package net.boomerangplatform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private static final String API_DOCS = "/apis/docs/**";

  private static final String HEALTH = "/health";

  @Bean
  SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(requests -> requests.requestMatchers("/",HEALTH, API_DOCS).permitAll());
    return httpSecurity.build();
  }

}
