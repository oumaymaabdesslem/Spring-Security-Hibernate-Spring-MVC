package com.example.springboot.service;

import com.example.springboot.entity.Role;

import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.PAForUserEnc;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche nom d'utilisateur dans notre base de données

        com.example.springboot.entity.User appuser = userRepository.findByUsername(username).
                orElseThrow(()->new UsernameNotFoundException("User is not exist"));
        // Créer la liste des rôles / accès dont disposent les utilisateurs
        Set List = new HashSet();
        for(Role role:appuser.getRoles()){
            GrantedAuthority grantedAuthority= new SimpleGrantedAuthority(role.getDescription());
            List.add(grantedAuthority);
        }


     // Créer et renvoyer un objet utilisateur pris en charge par Spring Security
        UserDetails user = (UserDetails) new User(username,appuser.getPassword(),List);
        return user;
    }

}
