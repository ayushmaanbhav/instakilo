package com.ayushmaanbhav.instakilo.sao;

import java.io.File;
import java.util.Set;

/**
 * Sao for S3
 * @author ayush
 */
public interface ImageSao {

    void saveImage(String username, File file);

    Set<String> getImagesForUser(String username);

}
