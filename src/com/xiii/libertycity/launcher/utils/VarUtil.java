package com.xiii.libertycity.launcher.utils;

public class VarUtil {

    public static VarUtil INSTANCE;
    public boolean isRunning = true;
    public boolean isAuthenticated = false;
    public String workDirectoryName = "libertycity";
    public String getPathToGameDirectory = "";

    public VarUtil() {
        INSTANCE = this;
    }
}
