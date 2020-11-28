package ua.coursework.secretfolder;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ua.coursework.secretfolder.fragments.LoginFragment;
import ua.coursework.secretfolder.fragments.LoginNewFragment;
import ua.coursework.secretfolder.utils.PINCrypter;

@RequiresApi(Build.VERSION_CODES.M)
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        PINCrypter.init(this);

        if (isPINSaved()) {
            openFragment(R.id.nav_host_fragment, new LoginFragment());
        } else {
            openFragment(R.id.nav_host_fragment, new LoginNewFragment());
        }

    }

    public boolean isPINSaved() {
        String pin = PINCrypter.getPin();
        return !"".equals(pin);
    }

    public void openFragment(int fragmentID, Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(fragmentID, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }


}



