package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import ua.coursework.secretfolder.BuildConfig;

public class PreferencesHandler {

    private static final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");
    private static SharedPreferences sharedPreferences = null;

    private static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    //For string value
    public static String getValue(Context context, String key, String defaultValue) {
        PreferencesHandler.openPref(context);
        String result = PreferencesHandler.sharedPreferences.getString(key, defaultValue);
        PreferencesHandler.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, String value) {
        PreferencesHandler.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = PreferencesHandler.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
        PreferencesHandler.sharedPreferences = null;
    }

    //You can create method like above for boolean, float, int etc...
}



