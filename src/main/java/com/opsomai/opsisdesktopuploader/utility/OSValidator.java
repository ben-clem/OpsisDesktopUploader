package com.opsomai.opsisdesktopuploader.utility;

public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String OSType;

    public static String getOSType() {

        System.out.println(OS);

        if (isWindows()) {
            System.out.println("This is Windows");
            OSType = "Windows";

        } else if (isMac()) {
            System.out.println("This is macOS");
            OSType = "macOS";

        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
            OSType = "Unix/Linux";

        } else if (isSolaris()) {
            System.out.println("This is Solaris");
            OSType = "Solaris";

        } else {
            System.out.println("Your OS is not support!!");
            OSType = "Unknown";

        }

        return OSType;
    }

    public static boolean isWindows() {

        return (OS.contains("win"));

    }

    public static boolean isMac() {

        return (OS.contains("mac"));

    }

    public static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));

    }

    public static boolean isSolaris() {

        return (OS.contains("sunos"));

    }

}
