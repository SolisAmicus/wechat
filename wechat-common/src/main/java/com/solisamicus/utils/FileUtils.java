package com.solisamicus.utils;

import java.util.UUID;

public class FileUtils {

    /**
     * Generates a new filename by appending a UUID to the original filename, preserving the original filename part.
     *
     * @param filename the original filename
     * @return the new filename with the UUID appended
     * @throws IllegalArgumentException if the filename is null or does not contain a period (.)
     */
    public static String generateFilenameWithUUID(String filename) {
        // Validate input
        validateFilename(filename);

        // Extract the suffix (file extension)
        String suffixName = getSuffix(filename);
        // Extract the original file name without the suffix
        String fName = getFileNameWithoutSuffix(filename);
        // Generate a random UUID
        String uuid = UUID.randomUUID().toString();

        // Using StringBuilder for efficient string concatenation
        return new StringBuilder(fName)
                .append("-")
                .append(uuid)
                .append(suffixName)
                .toString();
    }

    /**
     * Generates a new filename by replacing the original filename with a UUID, preserving the file extension.
     *
     * @param filename the original filename
     * @return the new filename with the UUID replacing the original filename
     * @throws IllegalArgumentException if the filename is null or does not contain a period (.)
     */
    public static String generateFilenameWithUUIDOnly(String filename) {
        // Validate input
        validateFilename(filename);

        // Extract the suffix (file extension)
        String suffixName = getSuffix(filename);
        // Generate a random UUID
        String uuid = UUID.randomUUID().toString();

        // Using StringBuilder for efficient string concatenation
        return new StringBuilder(uuid)
                .append(suffixName)
                .toString();
    }

    private static void validateFilename(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename. The filename must contain a period (.)");
        }
    }

    private static String getSuffix(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private static String getFileNameWithoutSuffix(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }
}
