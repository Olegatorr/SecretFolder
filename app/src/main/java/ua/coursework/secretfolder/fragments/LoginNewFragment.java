package ua.coursework.secretfolder.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import ua.coursework.secretfolder.GalleryActivity;
import ua.coursework.secretfolder.R;
import ua.coursework.secretfolder.utils.PINCrypter;
import ua.coursework.secretfolder.utils.UIHelper;

public class LoginNewFragment extends Fragment {

    private boolean isFirstPINEntered = false;
    private String firstPIN = "";
    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 3;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login_new, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText passField = (EditText) view.findViewById(R.id.passText);
        final TextView errorText = (TextView) view.findViewById(R.id.textError);
        final Button bLogin = (Button) view.findViewById(R.id.button_first);
        final TextView textViewSetUp = (TextView) view.findViewById(R.id.textViewSetUp);

        mFingerprintIdentify = new FingerprintIdentify(getContext());
        mFingerprintIdentify.setSupportAndroidL(true);
        mFingerprintIdentify.setExceptionListener(new BaseFingerprint.ExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                Log.e("FINGERPRINT", exception.getLocalizedMessage());
            }
        });
        mFingerprintIdentify.init();
        if (!mFingerprintIdentify.isFingerprintEnable()) {
            Log.w("FINGERPRINT", "Fingerprints are not available");
        }

        passField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
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

                if (!isFirstPINEntered) {
                    firstPIN = passField.getText().toString();
                    passField.getText().clear();
                    textViewSetUp.setText(R.string.confirm_pin);
                    isFirstPINEntered = true;
                } else {
                    if (passField.getText().toString().equals(firstPIN)) {
                        Snackbar.make(view, R.string.pin_set_up, Snackbar.LENGTH_SHORT)
                                .setAction(R.string.pin_set_up, null).show();

                        PINCrypter.setPin(firstPIN);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.do_you_want_to_use_fingerprint_to_login)
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        waitForFingerprint(true);
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        PINCrypter.setFingerAuth(false);
                                        dialog.dismiss();
                                        onLoginSetSuccess();
                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        errorText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void waitForFingerprint(Boolean bool) {

        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog = null;
        final UIHelper UIhelper = new UIHelper(getView(), getContext());
        if (bool) {
            alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(R.string.touch_sensor);
            alertDialogBuilder.setMessage(R.string.touch_the_sensor_to_be_able_to_use_fingerprints_to_login_into_the_SecretFolder).setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            final AlertDialog finalAlertDialog = alertDialog;
            mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.IdentifyListener() {

                @Override
                public void onSucceed() {
                    Log.i("FINGERPRINT", "Success");
                    PINCrypter.setFingerAuth(true);
                    UIhelper.showToastL(getString(R.string.fingerprint_login_enabled));
                    finalAlertDialog.dismiss();
                    onLoginSetSuccess();
                }

                @Override
                public void onNotMatch(int availableTimes) {
                    Log.w("FINGERPRINT", "Fingerprint not matched");
                    UIhelper.showToastS(getString(R.string.fingerprint_not_matched));
                }

                @Override
                public void onFailed(boolean isDeviceLocked) {
                    Log.e("FINGERPRINT", "Fingerprint recognition failed by user");
                    mFingerprintIdentify.cancelIdentify();
                    UIhelper.showToastL(getString(R.string.fingerprint_login_disabled));
                    finalAlertDialog.dismiss();
                    PINCrypter.setFingerAuth(false);
                    onLoginSetSuccess();
                }

                @Override
                public void onStartFailedByDeviceLocked() {
                    Log.e("FINGERPRINT", "Fingerprint recognition failed by device lock");
                    mFingerprintIdentify.cancelIdentify();
                    finalAlertDialog.dismiss();
                    PINCrypter.setFingerAuth(false);
                    onLoginSetSuccess();
                }
            });

        } else {
            alertDialog.dismiss();
        }
    }

    private void onLoginSetSuccess() {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
