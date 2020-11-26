package ua.coursework.secretfolder;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ua.coursework.secretfolder.fragments.LoginFragment;
import ua.coursework.secretfolder.fragments.LoginNewFragment;
import ua.coursework.secretfolder.utils.PreferencesHandler;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(isPINSaved()){
            openFragment(R.id.nav_host_fragment, new LoginFragment());
        }else{
            openFragment(R.id.nav_host_fragment, new LoginNewFragment());
        }

    }

    public Context getAppContext() {
        return getApplicationContext();
    }

    public boolean isPINSaved() {
        return PreferencesHandler.getValue(getAppContext(), "PIN", null) != null;
    }

    public void openFragment(int fragmentID, Fragment fragment){
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(fragmentID, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

}

