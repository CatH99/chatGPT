package com.FileProcess;

import java.io.File;


public class GetFileName {
    public static String extractFilenameFromPath(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastSeparatorIndex > 0 && lastDotIndex > lastSeparatorIndex) {
            return filePath.substring(lastSeparatorIndex + 1, lastDotIndex);
        }
        return null; // File path format is invalid
    }
}
