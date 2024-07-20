package com.solisamicus.constants;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Constants for QR code generation.
 */
public class QrCodeConstants {

    // Default file path for QR code images
    public static final String DEFAULT_FILE_PATH = "D:\\Temp\\qrcode\\qrcode.png";
    // Default width for QR code images
    public static final int DEFAULT_WIDTH = 300;
    // Default height for QR code images
    public static final int DEFAULT_HEIGHT = 300;
    // QR code error correction level
    public static final ErrorCorrectionLevel ERROR_CORRECTION_LEVEL = ErrorCorrectionLevel.H;
    // Character set used for QR code encoding
    public static final String CHARSET = "UTF-8";
    // QR code margin
    public static final int MARGIN = 1;
    // QR code image format
    public static final String IMAGE_FORMAT = "png";
}
