package com.example.mygoReaction.constant;

public interface Constant {
    // path
    String RESOURCEROOTPATH = "C:/Code/mygoReaction/asset/";
    String VIDEOFOLDERNAME = "video/";
    String SCREENCAPOUTPUTFOLDERNAME = "screen_cap/";
    String SUBTITLEFOLDERNAME = "subtitle/";
    
    // error message
    String SUBTITLEFILENOTFOUND = "Specified subtitle file not found.";
    String INAPPROPRIATESUBTITLEFILEFORMAT = "Inappropriate subtitle filename. Please rename the subtitle file with the following format:[series name]-[season]-[episode].[file extension]";

    // db related
    String CREATEDBY = "Spting Boot";

    interface extension{
        String MKV = "mkv";
        String PNG = "png";
    }
}
