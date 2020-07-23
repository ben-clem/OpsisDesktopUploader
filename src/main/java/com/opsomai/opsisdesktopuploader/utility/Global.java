package com.opsomai.opsisdesktopuploader.utility;

/**
 *
 */
public class Global {

//    // If building without packaging :
//    private static String workingDirPrefix = "src/main/";

    // If building through macOS packaging :
    private static String workingDirPrefix = "";
//
//    // If building through Windows packaging :
//    private static String workingDirPrefix = "?";
//
//    // If building through Linux / Unix packaging :
//    private static String workingDirPrefix = "?";
//
    public static String getWorkingDirPrefix() {
        return Global.workingDirPrefix;
    }

}
