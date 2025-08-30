package kz.kis.kiscore.utils;

import java.io.*;

public class UtKisFile {

    public static String deriveFileTypeFromFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "unknown";
        }
        String fileType = fileName.substring(dotIndex + 1).toLowerCase();
        return fileType;
    }

    public static boolean isPictureFile(String extension) throws Exception {
        if (extension == null || extension.length() == 0) {
            throw new Exception("No extension");
        }

        switch (extension) {
            case "jpg", "jpeg", "png", "bmp", "gif", "tif", "tiff":
                return true;
            default:
                return false;
        }
    }

    public static boolean isPdfFile(File inFile, String extension) {
        switch (extension) {
            case "pdf":
                return true;
            default:
                return false;
        }
    }

    public static boolean isExcelFile(String extension) {
        switch (extension) {
            case "xls", "xlsx": {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public static boolean isPrintableFile(File inFile, String extension) {
        switch (extension) {
            case "pdf", "docx", "doc", "xls", "xlsx", "ppt", "pptx", "txt", "rtf": {
                return true;
            }
            default: {
                return false;
            }
        }
    }


}
