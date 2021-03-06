package com.ayushmaanbhav.instakilo.route;

import java.util.Set;

import com.ayushmaanbhav.instakilo.entity.User;
import com.ayushmaanbhav.instakilo.sao.ImageSao;
import com.ayushmaanbhav.instakilo.util.UserAuthUtil;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Get images from S3
 * @author ayush
 */
public class ImageViewer implements Route {

    private final ImageSao imageSao;

    public ImageViewer(ImageSao imageSao) {
        this.imageSao = imageSao;
    }

    @Override
    public String handle(Request req, Response res) {

        if (!UserAuthUtil.isUserAuthenticated(req)) {
            res.status(401);
            return "Authentication required";
        }
        User user = UserAuthUtil.getAuthenticatedUser(req);

        Set<String> imageList = imageSao.getImagesForUser(user.getUsername());

        StringBuffer images = new StringBuffer("[");
        for (String imageUrl : imageList) {
            images.append("\"");
            images.append("");
            images.append(imageUrl);
            images.append("\",");
        }
        if (!imageList.isEmpty()) {
            images.deleteCharAt(images.length() - 1);
        }
        images.append("]");

        return images.toString();
    }

}
