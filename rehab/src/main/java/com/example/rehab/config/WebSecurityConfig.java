package com.example.rehab.config;

import com.example.rehab.models.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.example.rehab.models.enums.UserRole;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/working-with-patients/**").hasRole(String.valueOf(UserRole.DOCTOR))
                    .antMatchers("/events").hasRole(String.valueOf(UserRole.NURSE))
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/","/registration").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
        http.csrf().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("SELECT username, password, is_active FROM rehab_web_application.user WHERE username=?")
                .authoritiesByUsernameQuery("select u.username, ur.user_roles from user u inner join user_role ur on u.id = ur.user_id where u.username=?");

        auth.inMemoryAuthentication()
                .withUser("doctor")
                .password("{noop}doctor")
                .roles("DOCTOR")
                .and()
                .withUser("nurse")
                .password("{noop}nurse")
                .roles("NURSE", "DOCTOR");
    }


}