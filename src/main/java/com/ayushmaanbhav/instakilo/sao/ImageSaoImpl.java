package com.ayushmaanbhav.instakilo.sao;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.ayushmaanbhav.instakilo.config.AppConfig;
import com.ayushmaanbhav.instakilo.exception.RecoverableException;
import com.ayushmaanbhav.instakilo.util.KeyNameGenerator;

public class ImageSaoImpl implements ImageSao {

    private static final String SEPARATOR = "/";

    private final TransferManager transferManager;

    public ImageSaoImpl(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    @Override
    public void saveImage(String username, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(AppConfig.AWS_S3_BUCKET_NAME,
                        KeyNameGenerator.generate(username), file)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        Upload upload = transferManager.upload(putObjectRequest);

        try {
            upload.waitForCompletion();
        } catch (AmazonClientException | InterruptedException e) {
            throw new RecoverableException("Error uploading file to S3", e);
        }
    }

    @Override
    public Set<String> getImagesForUser(String username) {
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(AppConfig.AWS_S3_BUCKET_NAME)
                .withPrefix(username + SEPARATOR)
                .withDelimiter(SEPARATOR);

        // get listing upto 1000 keys if we want to get more then use
        // getNextBatch
        ListObjectsV2Result listing = transferManager.getAmazonS3Client().listObjectsV2(req);

        Set<String> setImages = new HashSet<>();
        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
            if (summary.getKey().endsWith("thumbnail")) {
                setImages.add("https://" + AppConfig.AWS_S3_BUCKET_NAME + ".s3.amazonaws.com/" + summary.getKey());
            }
        }

        return setImages;
    }

}
