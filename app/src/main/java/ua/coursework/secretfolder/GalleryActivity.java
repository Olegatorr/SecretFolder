package ua.coursework.secretfolder;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import ua.coursework.secretfolder.fragments.ViewFragment;
import ua.coursework.secretfolder.utils.CryptoHandler;
import ua.coursework.secretfolder.utils.PreferencesHandler;
import ua.coursework.secretfolder.utils.ProgressBarHelper;
import ua.coursework.secretfolder.utils.permissionsHandler;

public class GalleryActivity extends AppCompatActivity {

    AppCompatActivity activity;
    Context context;
    File mApplicationDirectory;
    File mApplicationDirectoryData;
    ProgressBarHelper progressBarHelper;
    FirebaseUser user;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Menu menu;
    FloatingActionButton fabBtn;

    CryptoHandler cryptoHandler;

    String filename = null;
    String picturePath = null;

    boolean isFirstGalleryOpen = true;
    boolean werePermissionsGranted = true;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        cryptoHandler = new CryptoHandler();


        setContentView(R.layout.activity_main);
        openFragment(R.id.nav_host_fragment, new ViewFragment());

        clearBackStackExclusive();

        if (!permissionsHandler.checkPermissions(this, context)){
            werePermissionsGranted = false;
        } else {
            werePermissionsGranted = true;
        }

        activity = this;

        mApplicationDirectory = context.getExternalFilesDir(null);
        mApplicationDirectoryData = new File(mApplicationDirectory + "/data");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (fabBtn == null){
            FloatingActionButton fab = activity.findViewById(R.id.fabAdd);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 699);
                    isFirstGalleryOpen = true;

                }
            });
            fabBtn = fab;
        }
        fabBtn.show();

        if (!werePermissionsGranted){
            werePermissionsGranted = true;
        } else {


            if (isFirstGalleryOpen) {
                isFirstGalleryOpen = false;
            } else {
                if (permissionsHandler.checkPermissions(this, context)) {
                    werePermissionsGranted = true;
                    lockApp();
                } else {
                    isFirstGalleryOpen = true;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void lockApp() {

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        androidx.core.content.ContextCompat.startActivity(context, intent, null);
        this.finish();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    restrictMenu();
                }else{
                    unRestrictMenu();
                }
            }
        });

        this.menu = menu;
        return true;
    }

    private void restrictMenu() {
        try {
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setEnabled(false);
            menu.getItem(5).setEnabled(false);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void unRestrictMenu(){
        try {
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(5).setEnabled(true);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        ViewFragment fragment = (ViewFragment) fm.findFragmentById(R.id.nav_host_fragment);
        ProgressBar progressBarTemp = fragment.getProgressBar();
        progressBarHelper = new ProgressBarHelper(progressBarTemp, getApplicationContext());

        File[] files = mApplicationDirectoryData.listFiles();

        if (id == R.id.action_settings) {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    69);

            return true;

        } else if (id == R.id.action_download) {

            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://secretfolder-dc714.appspot.com/" + PreferencesHandler.getValue(getApplicationContext(), "userID", "ND"));

            Task listAllTask = gsReference.listAll();

            listAllTask
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {

                            progressBarHelper.setMax(listResult.getItems().size());

                            Log.i("Firebase Download List Success", listResult.toString());

                            for (StorageReference prefix : listResult.getItems()) {

                                try {

                                    String fileName = prefix.getName();

                                    if (!new File(mApplicationDirectoryData + "/" + fileName).exists()) {

                                        File localFile = new File(mApplicationDirectoryData + "/" + fileName);
                                        if (!localFile.createNewFile()) {
                                            break;
                                        }
                                        prefix.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                progressBarHelper.add();
                                                Log.i("Firebase File Download", "SUCCESS");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                progressBarHelper.add();
                                                Log.e("Firebase File Download", "FAILURE: \n\n" + e.toString());
                                            }
                                        });
                                    } else {

                                        progressBarHelper.add();
                                        Log.i("Firebase Download", "FILE EXISTS");

                                    }
                                } catch (IOException e) {
                                    progressBarHelper.abort();
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBarHelper.abort();

                            Log.e("Firebase Download List failure", e.toString());
                        }
                    });

        } else if (id == R.id.action_upload) {

            progressBarHelper.setMax(files.length);

            for (File mFile : files) {

                Uri file = Uri.fromFile(mFile);
                StorageReference riversRef = mStorageRef.child(PreferencesHandler.getValue(getApplicationContext(), "userID", "ND") + "/" + file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                uploadTask.addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        progressBarHelper.add();
                        Log.e("Firebase", "Upload onFailure");

                    }

                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        progressBarHelper.add();
                        Log.i("Firebase", "Upload onSuccess");

                    }

                });
            }
        } else if (id == R.id.action_refresh) {
            Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
            startActivity(intent);
            activity.finish();
        } else if (id == R.id.logout) {
            signOut();
            delete();
            Toast.makeText(getApplicationContext(),
                    "Logout successful", Toast.LENGTH_LONG).show();

        } else if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openFragment(int fragmentID, Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(fragmentID, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

    public void clearBackStackExclusive() {
        getSupportFragmentManager().popBackStack("content_main", getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 69) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                PreferencesHandler.setValue(getApplicationContext(), "userID", user.getUid());

                Log.i("Firebase", "Login Successful");
            } else {
                Log.e("Firebase", "Login Failed: " + response.getErrorCode());
            }
        }else if(requestCode == 699){
            if (resultCode == RESULT_OK && null != data) {

                final Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                picturePath = cursor.getString(columnIndex);
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

                cursor.close();

                Bitmap bMap = null;
                try {
                    InputStream in = getContentResolver().openInputStream(selectedImage);
                    bMap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                writeFileOnInternalStorage(filename, convert(bMap));

            }
        }
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

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        byte[] encrypted = cryptoHandler.encrypt(context, base64);
        String encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);

        return encryptedString;
    }


}