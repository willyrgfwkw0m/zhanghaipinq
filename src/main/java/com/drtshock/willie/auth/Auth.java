package com.drtshock.willie.auth;

import com.drtshock.willie.Willie;
import org.pircbotx.User;

public class Auth {

    public static boolean checkAuth(User user) {
        return Willie.getInstance().getChannel("#Willie").getOps().contains(user);
    }
}
