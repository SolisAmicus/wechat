package com.solisamicus.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.solisamicus.constants.QrCodeConstants;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR codes.
 */
public class QrCodeUtils {

    /**
     * Generates a QR code with default size and file path.
     *
     * @param data The data to encode in the QR code.
     * @return The path to the generated QR code file.
     */
    public static String generateQRCode(String data) {
        return generateQRCode(data, QrCodeConstants.DEFAULT_FILE_PATH);
    }

    /**
     * Generates a QR code with default size and specified file path.
     *
     * @param data     The data to encode in the QR code.
     * @param filePath The path to save the QR code image.
     * @return The path to the generated QR code file.
     */
    public static String generateQRCode(String data, String filePath) {
        return generateQRCode(data, QrCodeConstants.DEFAULT_WIDTH, QrCodeConstants.DEFAULT_HEIGHT, filePath);
    }

    /**
     * Generates a QR code with specified size and file path.
     *
     * @param data     The data to encode in the QR code.
     * @param width    The width of the QR code image.
     * @param height   The height of the QR code image.
     * @param filePath The path to save the QR code image.
     * @return The path to the generated QR code file, or null if an error occurred.
     */
    public static String generateQRCode(String data, int width, int height, String filePath) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, QrCodeConstants.CHARSET);
            hints.put(EncodeHintType.ERROR_CORRECTION, QrCodeConstants.ERROR_CORRECTION_LEVEL);
            hints.put(EncodeHintType.MARGIN, QrCodeConstants.MARGIN);

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            File qrCodeFile = new File(filePath);
            ImageIO.write(image, QrCodeConstants.IMAGE_FORMAT, qrCodeFile);

            return filePath;
        } catch (WriterException e) {
            System.err.println("Failed to generate QR code: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to write QR code image to file: " + e.getMessage());
        }
        return null;
    }
}
