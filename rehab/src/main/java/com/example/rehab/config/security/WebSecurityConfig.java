package com.example.rehab.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private DataSource dataSource;

    private static final String ADMIN = "ADMIN";
    private static final String DOCTOR = "DOCTOR";
    private static final String NURSE = "NURSE";



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/patients/{\\d+}/add-procedure/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/{\\d+}/add-cure/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/{\\d+}/{\\d+}/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/new/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/discharge/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/return/**").hasAnyAuthority(DOCTOR,ADMIN)
                    .antMatchers("/patients/{\\d+}/**").hasAnyAuthority(DOCTOR,ADMIN,NURSE)
                    .antMatchers("/events/cancel/**").hasAnyAuthority(NURSE,ADMIN)
                    .antMatchers("/events/complete/**").hasAnyAuthority(NURSE,ADMIN)
                    .antMatchers("/events/hide/**").hasAnyAuthority(NURSE,ADMIN)
                    .antMatchers("/events/**").hasAnyAuthority(NURSE,ADMIN,DOCTOR)
                    .antMatchers("/admin/**").hasAnyAuthority(ADMIN)
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/","/about").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .exceptionHandling()
                .accessDeniedPage("/403");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("SELECT username, password, is_active FROM rehab_web_application.user WHERE username=?")
                .authoritiesByUsernameQuery("select u.username, ur.user_roles from user u inner join user_role ur on u.id = ur.user_id where u.username=?");



    }


}