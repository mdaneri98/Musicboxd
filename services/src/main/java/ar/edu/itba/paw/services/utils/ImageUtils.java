package ar.edu.itba.paw.services.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

    private ImageUtils() {
        // Utility class
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }

    public static byte[] resizeImage(byte[] originalImageBytes, int targetWidth, int targetHeight) {
        if (originalImageBytes == null || originalImageBytes.length == 0) {
            return originalImageBytes;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(originalImageBytes)) {
            BufferedImage originalImage = ImageIO.read(bais);

            if (originalImage == null) {
                LOGGER.warn("Could not read image bytes, returning original");
                return originalImageBytes;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            if (originalWidth <= targetWidth && originalHeight <= targetHeight) {
                return originalImageBytes;
            }

            // Calculate new dimensions maintaining aspect ratio
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > targetWidth) {
                newWidth = targetWidth;
                newHeight = (newWidth * originalHeight) / originalWidth;
            }

            if (newHeight > targetHeight) {
                newHeight = targetHeight;
                newWidth = (newHeight * originalWidth) / originalHeight;
            }

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // Write to byte array as JPG
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(resizedImage, "jpg", baos);
                return baos.toByteArray();
            }

        } catch (IOException e) {
            LOGGER.error("Error resizing image", e);
            return originalImageBytes;
        }
    }
}
