package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class UIHelper {

    View view;
    Context context;

    public UIHelper(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void showSnackbar(String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
                .setAction(text, null).show();
    }

    public void showToastL(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public void showToastS(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
