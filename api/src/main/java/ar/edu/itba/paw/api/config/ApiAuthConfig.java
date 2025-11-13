package ar.edu.itba.paw.api.config;

import ar.edu.itba.paw.api.filter.JwtAuthenticationFilter;
import ar.edu.itba.paw.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

import java.util.Arrays;
import java.util.List;


@EnableWebSecurity
@Configuration
public class ApiAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

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
                .antMatchers("GET", "/api/**").permitAll()
                
                // Moderator-only endpoints - Content Management
                .antMatchers("POST", "/api/artists").hasRole("MODERATOR")
                .antMatchers("PUT", "/api/artists/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/artists/*").hasRole("MODERATOR")
                .antMatchers("POST", "/api/artists/*/albums").hasRole("MODERATOR")
                .antMatchers("POST", "/api/albums").hasRole("MODERATOR")
                .antMatchers("PUT", "/api/albums/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/albums/*").hasRole("MODERATOR")
                .antMatchers("POST", "/api/albums/*/songs").hasRole("MODERATOR")
                .antMatchers("POST", "/api/songs").hasRole("MODERATOR")
                .antMatchers("PUT", "/api/songs/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/songs/*").hasRole("MODERATOR")
                .antMatchers("PATCH", "/api/reviews/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/users/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/reviews/*").hasRole("MODERATOR")
                .antMatchers("DELETE", "/api/comments/*").hasRole("MODERATOR")

                // User-only endpoints - Reviews and Comments
                .antMatchers("POST", "/api/reviews").hasRole("USER")
                .antMatchers("POST", "/api/reviews/*/comments").hasRole("USER")
                .antMatchers("PUT", "/api/reviews/*").hasRole("USER")
                .antMatchers("POST", "/api/reviews/*/likes").hasRole("USER")
                .antMatchers("DELETE", "/api/reviews/*/likes").hasRole("USER")
                .antMatchers("POST", "/api/comments").hasRole("USER")
                .antMatchers("PUT", "/api/comments/*").hasRole("USER")


                // User-only endpoints - User Profile and Social
                .antMatchers("PUT", "/api/users/*").hasRole("USER")
                .antMatchers("POST", "/api/users/*/followers").hasRole("USER")
                .antMatchers("DELETE", "/api/users/*/followers").hasRole("USER")
                
                // User-only endpoints - Favorites
                .antMatchers("POST", "/api/artists/*/favorite").hasRole("USER")
                .antMatchers("DELETE", "/api/artists/*/favorite").hasRole("USER")
                .antMatchers("POST", "/api/albums/*/favorite").hasRole("USER")
                .antMatchers("DELETE", "/api/albums/*/favorite").hasRole("USER")
                .antMatchers("POST", "/api/songs/*/favorite").hasRole("USER")
                .antMatchers("DELETE", "/api/songs/*/favorite").hasRole("USER")
                
                // Fallback: any other request requires authentication
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
