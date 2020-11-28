package ua.coursework.secretfolder.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;

import com.snatik.storage.Storage;

import java.io.File;

public class FileDeleter {

    ContentResolver contentResolver;
    Storage storage;

    public FileDeleter(Activity activity, Context context) {
        contentResolver = context.getContentResolver();
        permissionsHandler.checkPermissions(activity, context);
        storage = new Storage(context);
    }

    public void fDelete(final File file) {

        storage.deleteFile(file.getPath());

        /*
        if (file.exists()) {
            Log.i("fDeleter", "File exists");

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(file.delete()){
                        Log.i("fDeleter", "File Deleted Successfully");
                    }else {
                        Log.e("fDeleter", "File Not Deleted");
                    }

                    boolean tst2 = Files.isReadable(Paths.get(file.getPath()));
                    boolean tst3 = Files.isWritable(Paths.get(file.getPath()));

                    try {
                        Files.delete(Paths.get(file.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, 5000);

        }else{
            Log.w("fDeleter", "File not exists");
        }

         */
    }
}
