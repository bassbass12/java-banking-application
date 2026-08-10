package com.bassem.banking;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // ----------Hash Password-------

    public static String hashPassword(String password){

        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    // ----------Check Password-------

    public static boolean checkPassword(String password, String passwordHash){

        return BCrypt.checkpw(password, passwordHash);
    }


}
