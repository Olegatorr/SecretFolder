package ua.coursework.secretfolder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ua.coursework.secretfolder.fragments.ViewFragment;
import ua.coursework.secretfolder.utils.PreferencesHandler;
import ua.coursework.secretfolder.utils.ProgressBarHelper;
import ua.coursework.secretfolder.utils.UIHelper;
import ua.coursework.secretfolder.utils.permissionsHandler;

public class GalleryActivity extends AppCompatActivity {

    AppCompatActivity activity;
    File mApplicationDirectory;
    File mApplicationDirectoryData;
    ProgressBarHelper progressBarHelper;
    FirebaseUser user;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Menu menu;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        openFragment(R.id.nav_host_fragment, new ViewFragment());

        clearBackStackExclusive();

        permissionsHandler.checkPermissions(this, getApplicationContext());

        activity = this;

        mApplicationDirectory = getApplicationContext().getExternalFilesDir(null);
        mApplicationDirectoryData = new File(mApplicationDirectory + "/data");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

}