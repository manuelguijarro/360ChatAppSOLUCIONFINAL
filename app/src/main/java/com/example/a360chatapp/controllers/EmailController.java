package com.example.a360chatapp.controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailController {
    public static boolean comprobarEmail(String emailUsuario){
        if(!emailUsuario.isEmpty()){

            String expresionRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
            Pattern pattern = Pattern.compile(expresionRegex);
            Matcher matcher = pattern.matcher(emailUsuario);
            return matcher.matches();

        }else
            return false;

    }
}
