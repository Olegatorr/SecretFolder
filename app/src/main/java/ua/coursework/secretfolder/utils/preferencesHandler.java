package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ua.coursework.secretfolder.BuildConfig;

public class preferencesHandler {

    private static final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");
    private static SharedPreferences sharedPreferences = null;

    private static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    //For string value
    public static String getValue(Context context, String key, String defaultValue) {
        preferencesHandler.openPref(context);
        String result = preferencesHandler.sharedPreferences.getString(key, defaultValue);
        preferencesHandler.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, String value) {
        preferencesHandler.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = preferencesHandler.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
        preferencesHandler.sharedPreferences = null;
    }

    //You can create method like above for boolean, float, int etc...
}



