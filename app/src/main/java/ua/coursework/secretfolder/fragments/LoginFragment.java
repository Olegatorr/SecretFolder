package ua.coursework.secretfolder.fragments;

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

import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import ua.coursework.secretfolder.GalleryActivity;
import ua.coursework.secretfolder.R;
import ua.coursework.secretfolder.utils.PINCrypter;
import ua.coursework.secretfolder.utils.UIHelper;

public class LoginFragment extends Fragment {

    private static final int MAX_AVAILABLE_TIMES = 3;
    private FingerprintIdentify mFingerprintIdentify;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText passField = (EditText) view.findViewById(R.id.passText);
        final TextView errorText = (TextView) view.findViewById(R.id.textError);
        final Button bLogin = (Button) view.findViewById(R.id.button_first);
        final Button bFingerprint = (Button) view.findViewById(R.id.buttonFingerprint);

        PINCrypter.init(getContext());

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
            return;
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
                try {
                    if ((passField.getText().toString()).equals(PINCrypter.getPin())) {
                        onLoginSuccess();

                    } else {
                        errorText.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    errorText.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        });

        bFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("FINGERPRINT", "Click");

                waitForFingerprint(true);

            }
        });

        if (!PINCrypter.getFingerAuth())
            bFingerprint.setVisibility(View.GONE);

    }

    private void waitForFingerprint(Boolean bool) {

        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog = null;
        final UIHelper UIhelper = new UIHelper(getView(), getContext());
        if (bool) {
            alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle(getResources().getString(R.string.touch_sensor));
            alertDialogBuilder.setMessage(getResources().getString(R.string.touch_the_sensor_to_be_able_to_use_fingerprints_to_login_into_the_SecretFolder)).setCancelable(false);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            final AlertDialog finalAlertDialog = alertDialog;

            mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.IdentifyListener() {

                @Override
                public void onSucceed() {
                    Log.i("FINGERPRINT", "Success");
                    finalAlertDialog.dismiss();
                    onLoginSuccess();
                }

                @Override
                public void onNotMatch(int availableTimes) {
                    Log.w("FINGERPRINT", "Fingerprint not matched");
                    UIhelper.showToastS(getResources().getString(R.string.fingerprint_not_matched));
                }

                @Override
                public void onFailed(boolean isDeviceLocked) {
                    Log.e("FINGERPRINT", "Fingerprint recognition failed by user");
                    mFingerprintIdentify.cancelIdentify();
                    UIhelper.showToastL(getResources().getString(R.string.fingerprint_login_failed_Use_PIN));
                    finalAlertDialog.dismiss();
                }

                @Override
                public void onStartFailedByDeviceLocked() {
                    Log.e("FINGERPRINT", "Fingerprint recognition failed by device lock");
                    mFingerprintIdentify.cancelIdentify();
                    finalAlertDialog.dismiss();
                }
            });

        } else {
            alertDialog.dismiss();
        }
    }

    private void onLoginSuccess() {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintIdentify.cancelIdentify();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFingerprintIdentify.cancelIdentify();
    }
}
