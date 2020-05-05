package com.example.springboot;

import com.example.springboot.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    String [] ressourses = new String[]{
            "/include/**","/css/**","/icons/**","/img/**","/js/**","/layer/**"
    };


    protected  void  configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers(ressourses).permitAll()
                .antMatchers("/","/index").permitAll()
                .antMatchers("/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/userForm")
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .and()
                        .csrf().disable()
                .logout()
                        .permitAll()
                        .logoutSuccessUrl("/login?logout");
    }
     BCryptPasswordEncoder bCryptPasswordEncoder;
     @Bean
    public BCryptPasswordEncoder passwordEncoder(){
         bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        return bCryptPasswordEncoder;
    }
    // Précisez le responsable de la connexion et du cryptage du mot de passe
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    public void  configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
          auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
