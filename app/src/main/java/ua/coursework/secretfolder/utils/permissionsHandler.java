package ua.coursework.secretfolder.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import pub.devrel.easypermissions.EasyPermissions;

public class permissionsHandler {

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void checkPermissions(Activity activity, Context context) {

        if (!EasyPermissions.hasPermissions(context, REQUIRED_SDK_PERMISSIONS)) {
            EasyPermissions.requestPermissions(activity, "Access for storage",
                    101, REQUIRED_SDK_PERMISSIONS);
        }
    }
}
