package com.example.springboot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Passgenerator {

 public static void main(String ...args) {
   BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        // La chaîne que nous envoyons à la méthode d'encodage est le mot de passe que nous voulons crypter.
     System.out.println(bCryptPasswordEncoder.encode("1234"));
    }

}
