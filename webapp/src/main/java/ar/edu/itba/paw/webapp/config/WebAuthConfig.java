package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.CUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

@ComponentScan({ "ar.edu.itba.paw.webapp.auth" })
@EnableWebSecurity /* Default de web security */
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Value("classpath:rememberme.key")
    private Resource remembermeKey;

    @Autowired
    private CUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/")
                .and().authorizeRequests()
                .antMatchers("/user/verification", "/", "/user/forgot-password", "/user/reset-password", "/images/**", "/music").permitAll()
                .antMatchers("/user/register", "/user/login").anonymous()
                .antMatchers("/profile").hasRole("USER")
                .antMatchers("/mod/**").hasRole("MODERATOR")
                .anyRequest().authenticated()
                .and().formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/user/login")
                .defaultSuccessUrl("/", false)
                .and().rememberMe()
                .rememberMeParameter("remember_me")
                .key(new String(remembermeKey.getInputStream().readAllBytes()))
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .and().logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/login")
                .and().exceptionHandling()
                .accessDeniedPage("/403")
                .and().csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.ignoring().antMatchers("/css/**", "/js/**", "/assets/**", "favicon.ico", "/403");
        web.ignoring().antMatchers("/static/**", "/403");
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


}
