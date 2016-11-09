package com.ayushmaanbhav.instakilo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import com.ayushmaanbhav.instakilo.exception.RecoverableException;

import spark.Request;

public class FileUploader {

    public static File getFile(Request req, String fieldName) {

        if (req.raw().getAttribute("org.eclipse.jetty.multipartConfig") == null) {
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                    System.getProperty("java.io.tmpdir"));
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
        }

        try {
            Part file = req.raw().getPart(fieldName);
            String suffix = file.getSubmittedFileName().substring(file.getSubmittedFileName().lastIndexOf('.'));

            Path filePath = Paths.get(System.getProperty("java.io.tmpdir"),
                    String.join(".", UUID.randomUUID().toString(), suffix));

            Files.copy(file.getInputStream(), filePath);

            return filePath.toFile();
        } catch (IOException | ServletException e) {
            throw new RecoverableException("Error getting uploaded file", e);
        }

    }

}
