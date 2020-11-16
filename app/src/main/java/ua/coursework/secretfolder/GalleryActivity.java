package ua.coursework.secretfolder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ua.coursework.secretfolder.fragments.ViewFragment;

public class GalleryActivity extends AppCompatActivity {

    AppCompatActivity activity;
    File mApplicationDirectory;
    File mApplicationDirectoryData;
    private StorageReference mStorageRef;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openFragment(R.id.nav_host_fragment, new ViewFragment());

        clearBackStackExclusive();

        activity = this;

        mApplicationDirectory = getApplicationContext().getExternalFilesDir(null);
        mApplicationDirectoryData = new File(mApplicationDirectory + "/data");

        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        File[] files = mApplicationDirectoryData.listFiles();
        if(files == null){
            return false;
        }

        if (id == R.id.action_settings) {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

// Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    69);

            return true;

        }else if(id == R.id.action_download){

            //StorageReference listRef = storage.getReference().child("files/uid");

            for(File file : files){

            }

        }else if(id == R.id.action_upload){

            for(File mFile : files) {

                Uri file = Uri.fromFile(mFile);
                StorageReference riversRef = mStorageRef.child(file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                uploadTask.addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),
                                "Upload fail", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Upload onFailure");
                    }

                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),
                                "Upload success", Toast.LENGTH_SHORT).show();
                        Log.i("Firebase", "Upload onSuccess");
                    }

                });
            }
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 69) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("Firebase", "Login Successful");
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
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

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosUrl("https://example.com/terms.html")
                        .build(),
                69);
    }

}