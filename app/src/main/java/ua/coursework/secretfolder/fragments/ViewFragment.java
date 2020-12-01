package ua.coursework.secretfolder.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import ua.coursework.secretfolder.R;
import ua.coursework.secretfolder.utils.CryptoHandler;
import ua.coursework.secretfolder.utils.MyAdapter;
import ua.coursework.secretfolder.utils.permissionsHandler;

import static android.app.Activity.RESULT_OK;


public class ViewFragment extends Fragment {

    public AppCompatActivity activity;
    File mApplicationDirectory;
    File mApplicationDirectoryData;
    RecyclerView recyclerView;
    MyAdapter adapter;
    CryptoHandler cryptoHandler;
    String picturePath = null;
    String filename = null;

    FloatingActionButton fabBtn;

    ProgressBar progressBar;

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        View view = inflater.inflate(R.layout.fragment_view, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        mApplicationDirectory = getContext().getExternalFilesDir(null);
        mApplicationDirectoryData = new File(mApplicationDirectory + "/data");
        cryptoHandler = new CryptoHandler();

        progressBar = view.findViewById(R.id.progressBarUD);

        File folder = new File(mApplicationDirectoryData.getPath());
        if (!folder.exists()){
            folder.mkdir();
        }

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(activity, getContext());
        recyclerView.setAdapter(adapter);

        permissionsHandler.checkPermissions(getActivity(), getContext());

        loadImages();

        hideKeyboardFrom(getContext(), view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69 && resultCode == RESULT_OK && null != data) {

            final Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picturePath = cursor.getString(columnIndex);
            filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

            cursor.close();

            Bitmap bMap = null;
            try {
                InputStream in = getActivity().getContentResolver().openInputStream(selectedImage);
                bMap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage));
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeFileOnInternalStorage(filename, convert(bMap));

        }
    }

    public void onResume() {
        super.onResume();
        loadImages();
    }

    public Bitmap convert(String base64Str) throws IllegalArgumentException {
        try {
            byte[] test = Base64.decode(base64Str, Base64.DEFAULT);
            String decodedBytes = (cryptoHandler.decrypt(getContext(), test));
            byte[] base64Bytes = Base64.decode(decodedBytes, Base64.DEFAULT);
            Bitmap decoded = BitmapFactory.decodeByteArray(base64Bytes, 0, base64Bytes.length);

            return decoded;
        } catch (Exception e) {
            Log.e("Decoder", e.toString());

            return null;
        }
    }

    public String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        byte[] encrypted = cryptoHandler.encrypt(getContext(), base64);
        String encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);

        return encryptedString;
    }

    public void writeFileOnInternalStorage(String sFileName, String sBody) {
        File dir = mApplicationDirectoryData;
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            File gpxFile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxFile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        Map<Bitmap, String> images = getImages();
        adapter.clearItems();

        if (images != null) {
            adapter.setItems(images);
        }
    }

    private Map<Bitmap, String> getImages() {

        File[] files = mApplicationDirectoryData.listFiles();
        Map<Bitmap, String> images = new LinkedHashMap<Bitmap, String>();
        String temp = null;

        try {
            for (File file : files) {

                String fileAsString = null;
                try {
                    temp = "";
                    fileAsString = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
                    temp = file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = convert(fileAsString);
                if (bitmap != null) {
                    images.put(bitmap, temp);
                    Log.i("File: ", file.getAbsolutePath());
                } else {
                    Log.w("File: ", file.getAbsolutePath() + " had bad base64");
                }

            }
        } catch (NullPointerException e) {
            return null;
        }

        return images;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}


