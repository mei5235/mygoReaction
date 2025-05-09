package utils;

import java.util.Random;

public class FilenameUtils {
    public static String removeFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName; // No extension found
        }
        return fileName.substring(0, lastDotIndex);
    }

//    public static String getUniqueOutputFilename(String origName) {
//        int lastDotIndex = origName.lastIndexOf('.');
//        String[] filenameNext = {origName.substring(0,lastDotIndex),origName.substring(lastDotIndex)};
//        String[] range = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split("");
//        StringBuilder sb = new StringBuilder().append(filenameNext[0]).append("_");
//        Random rng = new Random();
//        for (int i = 0; i<8; i++){
//            sb.append(range[rng.nextInt(62)]);
//        }
//        return sb.append(filenameNext[1]).toString();
//    }

    public static String getUniqueOutputFilename(String origname) {
        int lastDoxIndex = origname.lastIndexOf(".");
        String[] range = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".split("");
        StringBuilder sb = new StringBuilder();
        Random rng = new Random();
        if (lastDoxIndex < 0){
            sb.append(origname).append("_");
            for (int i = 0; i<8; i++){
                sb.append(range[rng.nextInt(62)]);
            }
            return sb.toString();
        }
        for (int i = 0; i<8; i++){
            sb.insert(lastDoxIndex, range[rng.nextInt(62)]);
        }
        sb.insert(lastDoxIndex, "_");
        return sb.toString();
    }
}
