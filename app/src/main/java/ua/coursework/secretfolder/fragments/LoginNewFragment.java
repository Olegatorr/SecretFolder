package ua.coursework.secretfolder.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import ua.coursework.secretfolder.LoginActivity;
import ua.coursework.secretfolder.R;
import ua.coursework.secretfolder.utils.md5Calculator;
import ua.coursework.secretfolder.utils.preferencesHandler;

public class LoginNewFragment extends Fragment {

    private boolean isFirstPINEntered = false;
    private String firstPIN = "";
    final private md5Calculator md5 = new md5Calculator();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_new, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText passField = (EditText)view.findViewById(R.id.passText);
        final TextView errorText = (TextView)view.findViewById(R.id.textError);
        final Button bLogin = (Button)view.findViewById(R.id.button_first);
        final FloatingActionButton bAdd = (FloatingActionButton)view.findViewById(R.id.fabAdd);
        final TextView textViewSetUp = (TextView)view.findViewById(R.id.textViewSetUp);

        ua.coursework.secretfolder.utils.preferencesHandler preferences =
                new ua.coursework.secretfolder.utils.preferencesHandler();

        passField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            bLogin.callOnClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isFirstPINEntered){
                    firstPIN = passField.getText().toString();
                    passField.getText().clear();
                    textViewSetUp.setText(R.string.confirm_pin);
                    isFirstPINEntered = true;
                }else{
                    if(passField.getText().toString().equals(firstPIN)){
                        Snackbar.make(view, R.string.pin_set_up, Snackbar.LENGTH_SHORT)
                                .setAction(R.string.pin_set_up, null).show();

                        preferencesHandler.setValue(getContext(), "PIN", md5.md5Apache(passField.getText().toString()));

                        ((LoginActivity)getActivity()).openFragment(R.id.nav_host_fragment, new ViewFragment());
                        //((MainActivity)getActivity()).clearBackStack();

                    }else{
                        errorText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
