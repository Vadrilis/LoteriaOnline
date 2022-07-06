package br.com.edu.ifpb.tsi.pwebii.projeto.loteriaonline.avcobloteriaonline.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/css/**", "/imagens/**",  "/acesso-negado", "/auth/**", "/clientes", "/fragments/**","/sorteio/**").permitAll() //TODO coloquei esse auth dps
            .antMatchers("/sorteios/").hasAnyRole("ADMIN", "CLIENTE")
            .antMatchers("/clientes/**").hasRole("ADMIN")
            .anyRequest()
            .authenticated()
            .and()
            .formLogin(form -> form
                    .loginPage("/auth")
                    .defaultSuccessUrl("/home", true)
                    .permitAll())
            .logout(logout -> logout.logoutUrl("/auth/logout"))
            .exceptionHandling().accessDeniedPage("/acesso-negado");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(encoder)
        // .withUser(
        // User.builder().username("dri").password(encoder.encode("dri")).roles("CLIENTE").build())
        // .withUser(User.builder().username("van").password(encoder.encode("dri")).roles("CLIENTE").build())
        // .withUser(User.builder().username("lis").password(encoder.encode("lis"))
        // .roles("CLIENTE", "ADMIN").build())
        ;
    }
}