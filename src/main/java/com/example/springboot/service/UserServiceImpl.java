package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.exception.CustomeFieldValidationException;
import com.example.springboot.mdp.ChangePasswordForm;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public Iterable<User> getAllUsers(){

        return userRepository.findAll();
    }


     // si username est occupé par autre utilisteur(validation)
    public boolean checkUsernameAvailable(User user) throws Exception{
       Optional<User> userFound =userRepository.findByUsername(user.getUsername());
       if(userFound.isPresent()){
           throw  new CustomeFieldValidationException("Username is not available","username");
       }
        return true;
    }
     //si le mot de passe n'est pas egaux
    public boolean checkPasswordValid(User user) throws  Exception{
        if(user.getConfirmPassword()== null || user.getConfirmPassword().isEmpty()){
            throw new CustomeFieldValidationException("Confirm password is obligatory","confirmPassword");

        }
        if(!user.getPassword().equals(user.getConfirmPassword())){
           throw  new CustomeFieldValidationException("Password is not confirmed","password");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception {
        if(checkUsernameAvailable(user) && checkPasswordValid(user)){
            String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        User user =userRepository.findById(id).orElseThrow(()-> new Exception("User does not exit"));
        return user;
    }

    @Override
    public User updateUser(User userfrom) throws Exception {
        User userto= getUserById(userfrom.getId());
        mapUser(userfrom,userto);
        return userRepository.save(userto);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUser(Long id) throws Exception {
        User user= userRepository.findById(id)
                .orElseThrow(()->new  Exception("User not Found in deleteUser -"+ this.getClass().getName()));
          userRepository.delete(user);
    }



    /**
     * Map everythin but the password.
     * @param from
     * @param to
     */
    protected void mapUser(User from,User to) {
        to.setUsername(from.getUsername());
        to.setFirstname(from.getFirstname());
        to.setLastname(from.getLastname());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }


    @Override
    public User ChangePassword(ChangePasswordForm form) throws Exception {
        User user = userRepository.findById(form.getId()).
                orElseThrow(()->new Exception("User not Found in ChangePassword -"+ this.getClass().getName()));
        if(!isLoggedUserADMIN() && form.getCurrentPassword().equals(user.getPassword())){
            throw  new Exception("Current Password Incorrect.");
        }
        if ( form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new Exception("New Password must be different than Current Password!");
        }

        if( !form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new Exception("New Password and Confirm Password does not match!");
        }
        String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    private boolean isLoggedUserADMIN() {

        // Récupère l'utilisateur connecté
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggeduser = null;
        Object roles = null;

        // Vérifiez que cet objet de session récupéré est l'utilisateur
        if(principal instanceof  UserDetails){
            loggeduser = (UserDetails) principal;
            roles = loggeduser.getAuthorities().stream()
                    .filter(x->"ROLE_ADMIN".equals(((GrantedAuthority) x).getAuthority()))
                    .findFirst().orElse(null);
        }

        return  roles != null ? true : false;
    }

    private User getLoggedUser() throws Exception{
        Object principla= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggeduser =null;
        Object roles =null;

        if(principla instanceof  UserDetails){
            loggeduser=(UserDetails) principla;
        }
        User myuser = userRepository.findByUsername(loggeduser.getUsername()).orElseThrow(() -> new Exception(""));
        return myuser;
    }

}
