package com.example.mygoReaction.constant;

public interface Constant {
    // path
    String MYGO_REACTION_ASSET = "C:/Code/mygoReaction/asset/";
    String VIDEO_FOLDERNAME = "video/";
    String SCREEN_CAP = "screen_cap/";
    String SUBTITLE＿FOLDERNAME = "subtitle/";
    
    // message
    String SUBTITLEFILENOTFOUND = "Specified subtitle file not found.";
    String INAPPROPRIATE_SUBTITLE_FILE_FORMAT = "Inappropriate subtitle filename. Please rename the subtitle file with the following format:[series name]-[season]-[episode].[file extension]";
    String INSUFFICIENT＿AUGMENT＿DETERMIND＿SEARCH＿METHOD = "Missing augments. Cannot determine searching criteria since both line and timestamp is empty.";
    String MISSING＿SERIES＿INFO = "Missing augments. Insufficient info for finding anime series.";
    String NO＿RECORD = "No Record found.";
    String SUCCESS = "Success.";
    // db related
    String CREATEDBY = "Spting Boot";

    interface extension{
        String MKV = "mkv";
        String PNG = "png";
    }
}
