package com.ayushmaanbhav.instakilo.route;

import java.io.IOException;

import com.ayushmaanbhav.instakilo.util.UserAuthUtil;

import lombok.extern.log4j.Log4j;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;

/**
 * Main page
 * @author ayush
 */
@Log4j
public class MainPageController implements Route {

    public MainPageController() {
    }

    @Override
    public String handle(Request req, Response res) {

        try {
            if (!UserAuthUtil.isUserAuthenticated(req)) {
                return IOUtils.toString(Spark.class.getResourceAsStream("/restricted/login.html"));
            }

            return IOUtils.toString(Spark.class.getResourceAsStream("/restricted/main.html"));
        } catch (IOException e) {
            log.error("Exception occurred when getting data", e);
        }

        res.status(500);
        return "Internal Error";
    }

}
