package ua.coursework.secretfolder.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ua.coursework.secretfolder.MainActivity;
import ua.coursework.secretfolder.MyAdapter;
import ua.coursework.secretfolder.R;

public class ViewFragment extends Fragment {

    File mApplicationDirectory;
    File mApplicationDirectoryData;
    RecyclerView recyclerView;
    MyAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_view, container, false);

        mApplicationDirectory = getContext().getExternalFilesDir(null);
        mApplicationDirectoryData = new File(mApplicationDirectory + "/data");

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        recyclerView.setLayoutManager(layoutManager);
        //ArrayList<String> createLists = prepareData();
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        loadImages();

        ((MainActivity)getActivity()).checkPermissions();

        ((MainActivity)getActivity()).showFab();

        hideKeyboardFrom(getContext(), view);
    }

    public void onResume(){
        super.onResume();
        loadImages();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadImages(){
        Collection<String> images = getImages();
        adapter.clearItems();

        if(images != null){
            adapter.setItems(images);
        }
    }

    private Collection<String> getImages(){

        File[] files = mApplicationDirectoryData.listFiles();
        List<String> images = new ArrayList<String>();

        try {
            for (File file : files) {

                String fileAsString = null;
                try {
                    fileAsString = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Bitmap bitmap = convert(fileAsString);
                images.add(fileAsString);
                Log.i("File: ", file.getAbsolutePath());
            }
        }catch (NullPointerException e){
            return null;
        }

        return images;
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


}


