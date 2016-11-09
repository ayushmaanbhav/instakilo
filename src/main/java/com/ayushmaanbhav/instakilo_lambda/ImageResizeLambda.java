package com.ayushmaanbhav.instakilo_lambda;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.imageio.ImageIO;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Lambda function to create thumbnails
 * 
 * @author ayush
 */
public class ImageResizeLambda implements RequestHandler<S3Event, String> {

    private static final float MAX_WIDTH = 640;
    private static final float MAX_HEIGHT = 480;

    private final String JPG_MIME = (String) "image/jpeg";

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();
            String srcKey = record.getS3().getObject().getKey();
            srcKey = URLDecoder.decode(srcKey, "UTF-8");

            String dstKey = srcKey + "-thumbnail";

            AmazonS3 s3Client = new AmazonS3Client()
                    .withRegion(Regions.AP_SOUTHEAST_1);
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));
            InputStream objectData = s3Object.getObjectContent();

            BufferedImage srcImage = ImageIO.read(objectData);
            int srcHeight = srcImage.getHeight();
            int srcWidth = srcImage.getWidth();
            // Infer the scaling factor to avoid stretching the image
            // unnaturally
            float scalingFactor = Math.min(MAX_WIDTH / srcWidth, MAX_HEIGHT / srcHeight);
            int width = (int) (scalingFactor * srcWidth);
            int height = (int) (scalingFactor * srcHeight);

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            // Fill with white before applying semi-transparent (alpha) images
            g.setPaint(Color.white);
            g.fillRect(0, 0, width, height);
            // Simple bilinear resize
            // If you want higher quality algorithms, check this link:
            // https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImage, 0, 0, width, height, null);
            g.dispose();

            // Re-encode image to target format
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            // Set Content-Length and Content-Type
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(os.size());
            meta.setContentType(JPG_MIME);

            // Uploading to S3 destination bucket
            System.out.println("Writing to: " + srcBucket + "/" + dstKey);

            PutObjectRequest putObjectRequest = new PutObjectRequest(srcBucket, dstKey, is, meta)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);

            System.out.println("Successfully resized " + srcBucket + "/" + srcKey + " and uploaded to " + srcBucket
                    + "/" + dstKey);

            return "Ok";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}