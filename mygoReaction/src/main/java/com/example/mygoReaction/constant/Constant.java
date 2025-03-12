package com.example.mygoReaction.constant;

public interface Constant {
    // path
    String resourceRootPath = "C:/Code/mygoReaction/asset";
    String videoFolderName = "video/";
    String screenCapOutputFolderName = "screen_cap/";
    String subtitleFolderName = "subtitle/";
    
    // error message
    String subtitleFileNotFound = "Specified subtitle file not found.";
    String inappropriateSubtitleFileFormat = "Inappropriate subtitle filename. Please rename the subtitle file with the following format:[series name]-[season]-[episode].[file extension]";

    // db related
    String createdBy = "Spting Boot";

    String extension = "mkv";
}
