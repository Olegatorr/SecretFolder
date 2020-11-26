package ua.coursework.secretfolder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView image1 = findViewById(R.id.imageView1);
        ImageView image2 = findViewById(R.id.imageView2);
        ImageView image3 = findViewById(R.id.imageView3);
        ImageView image4 = findViewById(R.id.imageView4);
        ImageView image5 = findViewById(R.id.imageView5);
        ImageView image6 = findViewById(R.id.imageView6);
        ImageView image7 = findViewById(R.id.imageView7);
        ImageView image8 = findViewById(R.id.imageView8);
        ImageView image9 = findViewById(R.id.imageView9);

        textView = findViewById(R.id.editTextTextPersonName);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 1);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 2);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 3);
            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 4);
            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 5);
            }
        });

        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 6);
            }
        });

        image7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 7);
            }
        });

        image8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 8);
            }
        });

        image9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShortcutToHomeScreen(getApplicationContext(), 9);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void addShortcutToHomeScreen(Context context, int number)
    {

        int drawable;
        switch (number){
            case 1:
                drawable = R.drawable.application;
                break;
            case 2:
                drawable = R.drawable.application_1_;
                break;
            case 3:
                drawable = R.drawable.apps;
                break;
            case 4:
                drawable = R.drawable.feature;
                break;
            case 5:
                drawable = R.drawable.folder;
                break;
            case 6:
                drawable = R.drawable.google_docs;
                break;
            case 7:
                drawable = R.drawable.google_docs_1_;
                break;
            case 8:
                drawable = R.drawable.resume;
                break;
            case 9:
                drawable = R.drawable.setting;
                break;
            default:
                drawable = R.drawable.folder;
                break;
        }

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context))
        {
            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, "#1")
                    .setIntent(new Intent(context, LoginActivity.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                    .setShortLabel(textView.getText())
                    .setIcon(IconCompat.createWithResource(context, drawable))
                    .build();
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
        }
        else
        {
            Snackbar.make(findViewById(R.id.settings), R.string.shortcuts_not_supported, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.shortcuts_not_supported, null).show();
        }
    }
}