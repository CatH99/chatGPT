package com.FileProcess;

import java.io.File;

public class DeleteFile {
    public static void deleteFile(String filePath) {
    	File file = new File(filePath);
    	if (file.delete()) { 
    	      System.out.println("Deleted the file: " + file.getName());
    	    } else {
    	      System.out.println("Failed to delete the file.");
    	    } 
    }
}
