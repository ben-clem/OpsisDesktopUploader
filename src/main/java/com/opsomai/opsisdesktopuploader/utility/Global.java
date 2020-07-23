package com.opsomai.opsisdesktopuploader.utility;

/**
 *
 */
public class Global {

//    // If building through IDE:
//    private static String workingDirPrefix = "src/main/";
    //
    // If building through macOS or Windows packaging:
    private static String workingDirPrefix = "";
//

    public static String getWorkingDirPrefix() {
        return Global.workingDirPrefix;
    }

}
