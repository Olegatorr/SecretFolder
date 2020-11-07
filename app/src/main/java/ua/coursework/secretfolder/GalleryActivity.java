package ua.coursework.secretfolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import ua.coursework.secretfolder.fragments.ViewFragment;
import ua.coursework.secretfolder.utils.MyAdapter;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment(R.id.nav_host_fragment, new ViewFragment());

        clearBackStackExclusive();

    }

    public void openFragment(int fragmentID, Fragment fragment){
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(fragmentID, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

    public void clearBackStackExclusive(){
        getSupportFragmentManager().popBackStack("content_main", getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }

    public Context getAppContext() {
        return getApplicationContext();
    }
}