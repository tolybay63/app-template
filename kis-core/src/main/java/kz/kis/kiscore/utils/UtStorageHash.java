package kz.kis.kiscore.utils;

public class UtStorageHash {

    public static String getPageName(String fileHash, Integer page) {
        return fileHash + "_" + page;
    }

}