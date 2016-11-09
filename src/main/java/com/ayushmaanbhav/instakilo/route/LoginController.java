package com.ayushmaanbhav.instakilo.route;

import com.ayushmaanbhav.instakilo.config.AppConfig;
import com.ayushmaanbhav.instakilo.entity.User;
import com.ayushmaanbhav.instakilo.util.UserAuthUtil;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Login user
 * 
 * @author ayush
 */
public class LoginController implements Route {

    public LoginController() {
    }

    @Override
    public String handle(Request req, Response res) {

        String username = req.queryParams("username");
        String password = req.queryParams("password");

        if (AppConfig.USERNAME.equals(username) && AppConfig.PASSWORD.equals(password)) {
            UserAuthUtil.putAuthenticatedUser(req, new User(username, password));
            res.redirect("/");
            return "Done!";
        }

        res.status(401);
        return "Authentication failure!";
    }

}
