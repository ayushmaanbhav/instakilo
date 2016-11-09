package com.ayushmaanbhav.instakilo.util;

import java.util.UUID;

/**
 * Key Generator S3 files
 * @author ayush
 */
public class KeyNameGenerator {

    private static final String SEPARATOR = "/";

    public static String generate(String username) {
        return String.join(SEPARATOR, username, UUID.randomUUID().toString() + "-image");
    }

}
