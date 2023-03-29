package com.xiii.libertycity.launcher.utils;

public class VarUtil {

    public static VarUtil INSTANCE;
    public boolean isRunning = true;
    public boolean isAuthenticated = false;

    public VarUtil() {
        INSTANCE = this;
    }
}
