package com.solisamicus.utils;

public class DesensitizationUtil {

    private static final int MASK_SIZE = 6;
    private static final String MASK_SYMBOL = "*";

    public static String commonDisplay(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        int length = value.length();
        int halfLength = length / 2;
        int middle = halfLength - 1;
        int remainder = length % 2;

        if (length <= 2) {
            return length == 1 ? MASK_SYMBOL : value.charAt(0) + MASK_SYMBOL;
        }

        StringBuilder maskedValue = new StringBuilder();

        if (middle <= 0) {
            maskedValue.append(value.charAt(0))
                    .append(MASK_SYMBOL)
                    .append(value.charAt(length - 1));
        } else if (middle >= MASK_SIZE / 2 && MASK_SIZE + 1 != length) {
            int start = (length - MASK_SIZE) / 2;
            maskedValue.append(value.substring(0, start))
                    .append(MASK_SYMBOL.repeat(MASK_SIZE));
            int endStart = length - start;
            if (remainder == 0 && MASK_SIZE % 2 == 0 || remainder != 0 && MASK_SIZE % 2 != 0) {
                maskedValue.append(value.substring(endStart, length));
            } else {
                maskedValue.append(value.substring(endStart - 1, length));
            }
        } else {
            int middleMaskLength = length - 2;
            maskedValue.append(value.charAt(0))
                    .append(MASK_SYMBOL.repeat(middleMaskLength))
                    .append(value.charAt(length - 1));
        }

        return maskedValue.toString();
    }
}
