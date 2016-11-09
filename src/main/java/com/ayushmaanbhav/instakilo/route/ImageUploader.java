package com.ayushmaanbhav.instakilo.route;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.ayushmaanbhav.instakilo.config.AppConfig;
import com.ayushmaanbhav.instakilo.entity.User;
import com.ayushmaanbhav.instakilo.exception.RecoverableException;
import com.ayushmaanbhav.instakilo.sao.ImageSao;
import com.ayushmaanbhav.instakilo.util.FileUploader;
import com.ayushmaanbhav.instakilo.util.UserAuthUtil;

import lombok.extern.log4j.Log4j;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Upload image to s3
 * 
 * @author ayush
 */
@Log4j
public class ImageUploader implements Route {

    private final ImageSao imageSao;

    public ImageUploader(ImageSao imageSao) {
        this.imageSao = imageSao;
    }

    @Override
    public String handle(Request req, Response res) {

        if (!UserAuthUtil.isUserAuthenticated(req)) {
            res.status(401);
            return "Authentication required";
        }
        User user = UserAuthUtil.getAuthenticatedUser(req);

        File image = null;
        try {
            image = FileUploader.getFile(req, "file");
        } catch (RecoverableException re) {
            log.error("Error getting uploaded file", re);
            res.status(500);
            return re.getLocalizedMessage();
        }

        if (!validateImage(image)) {
            res.status(415);
            return "Image format not supported";
        }

        try {
            imageSao.saveImage(user.getUsername(), image);
        } catch (RecoverableException re) {
            log.error("Error uploading file", re);
            res.status(500);
            return re.getLocalizedMessage();
        }

        res.status(200);
        return "Done!";
    }

    private boolean validateImage(File file) {

        if (file.length() > AppConfig.MAX_FILE_SIZE) {
            return false;
        }

        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {

            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

            while (imageReaders.hasNext()) {
                ImageReader reader = (ImageReader) imageReaders.next();
                if (AppConfig.SUPPORTED_IMAGE_FORMATS.contains(reader.getFormatName())) {
                    return true;
                }
            }

        } catch (IOException e) {
            log.warn("Error reading image", e);
        }
        return false;
    }

}
