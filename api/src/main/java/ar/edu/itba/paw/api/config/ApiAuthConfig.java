package ar.edu.itba.paw.api.config;

import ar.edu.itba.paw.api.filter.JwtAuthenticationFilter;
import ar.edu.itba.paw.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApiAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtService jwtService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Public endpoints
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                
                // ROLE_MODERATOR-only endpoints - Content Management
                .antMatchers(HttpMethod.POST, "/api/artists").hasRole("MODERATOR")
                .antMatchers(HttpMethod.PUT, "/api/artists/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/artists/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.POST, "/api/artists/*/albums").hasRole("MODERATOR")
                .antMatchers(HttpMethod.POST, "/api/albums").hasRole("MODERATOR")
                .antMatchers(HttpMethod.PUT, "/api/albums/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/albums/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.POST, "/api/albums/*/songs").hasRole("MODERATOR")
                .antMatchers(HttpMethod.POST, "/api/songs").hasRole("MODERATOR")
                .antMatchers(HttpMethod.PUT, "/api/songs/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/songs/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.PATCH, "/api/reviews/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/reviews/*").hasRole("MODERATOR")
                .antMatchers(HttpMethod.DELETE, "/api/comments/*").hasRole("MODERATOR")
                
                // ROLE_USER-only endpoints - Reviews and Comments
                .antMatchers(HttpMethod.POST, "/api/reviews").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/reviews/*/comments").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/api/reviews/*").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/reviews/*/likes").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/reviews/*/likes").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/comments").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/api/comments/*").hasRole("USER")

                // ROLE_USER-only endpoints - User Profile and Social
                .antMatchers(HttpMethod.PUT, "/api/users/*").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/users/*/followers").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/users/*/followers").hasRole("USER")
                
                // ROLE_USER-only endpoints - Favorites
                .antMatchers(HttpMethod.POST, "/api/artists/*/favorite").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/artists/*/favorite").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/albums/*/favorite").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/albums/*/favorite").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/songs/*/favorite").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/songs/*/favorite").hasRole("USER")
                
                // Fallback: any other request requires authentication
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                })
                .accessDeniedHandler((request, response, ex) -> {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden");
                })

                // Disable client-side cache handling
                .and().headers().cacheControl().disable()

                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    
                // Enable CORS and disable csrf rules
                .cors().and().csrf().disable();

    }
}
