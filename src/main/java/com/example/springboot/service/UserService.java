package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.mdp.ChangePasswordForm;

public interface UserService {
    public Iterable<User> getAllUsers();

    public User createUser(User user) throws Exception;

    public User getUserById(Long id) throws Exception;

    public User updateUser(User user) throws Exception;

    public void deleteUser(Long id) throws Exception;

    public User ChangePassword(ChangePasswordForm form) throws  Exception;
}
