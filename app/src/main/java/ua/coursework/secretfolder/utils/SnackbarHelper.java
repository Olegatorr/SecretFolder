package ua.coursework.secretfolder.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarHelper {

    View view;

    public SnackbarHelper(View view){
        this.view = view;
    }

    public void show(String text){
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                .setAction(text, null).show();
    }

}
