package com.ayushmaanbhav.instakilo.util;

import com.ayushmaanbhav.instakilo.entity.User;

import spark.Request;

/**
 * Get user from session
 * @author ayush
 */
public class UserAuthUtil {

    private static final String USER_SESSION_ID = "USER_SESSION_ID";

    public static boolean isUserAuthenticated(Request req) {
        return req.session().attribute(USER_SESSION_ID) != null;
    }

    public static User getAuthenticatedUser(Request req) {
        return req.session().attribute(USER_SESSION_ID);
    }

    public static void putAuthenticatedUser(Request req, User user) {
        req.session().attribute(USER_SESSION_ID, user);
    }

}
