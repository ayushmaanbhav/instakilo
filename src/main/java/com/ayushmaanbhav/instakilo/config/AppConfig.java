package com.ayushmaanbhav.instakilo.config;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * AppConfig for the application
 * @author ayush
 */
public class AppConfig {

    public static final String AWS_S3_BUCKET_NAME = "lambda-function-instakilo";

    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final Set<String> SUPPORTED_IMAGE_FORMATS = ImmutableSet.of("JPEG", "PNG", "JPG");

    public static final String USERNAME = "instakilo@username";
    public static final String PASSWORD = "instakilo@pass1993";

    public static void init() {

    }

}
