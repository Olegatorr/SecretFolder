package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Objects;

import javax.crypto.Cipher;

import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.impl.Base64CipherCrypter;
import co.infinum.goldfinger.crypto.impl.UnlockedAesCipherFactory;
import ua.coursework.secretfolder.BuildConfig;

@RequiresApi(Build.VERSION_CODES.M)
public class PINCrypter {

    private static final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");
    private static SharedPreferences PREFS;
    private static CipherCrypter CRYPTER;
    private static CipherFactory FACTORY;

    @Nullable
    public static String getPin() {
        String encryptedPin = PREFS.getString("pin", "");
        if ("".equals(encryptedPin)) {
            return "";
        }

        Cipher cipher = FACTORY.createDecryptionCrypter("pin");
        if (cipher == null) {
            return "";
        }

        return CRYPTER.decrypt(cipher, encryptedPin);
    }

    public static void setPin(String pin) {
        Cipher cipher = FACTORY.createEncryptionCrypter("pin");
        if (cipher == null) {
            return;
        }

        String encryptedPin = CRYPTER.encrypt(cipher, pin);
        PREFS.edit().putString("pin", encryptedPin).apply();

    }

    public static void init(Context context) {
        PREFS = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        CRYPTER = new Base64CipherCrypter();
        FACTORY = new UnlockedAesCipherFactory(context);
    }

    public static boolean getFingerAuth() {
        String encryptedPin = PREFS.getString("finger", "");
        if ("".equals(encryptedPin)) {
            return false;
        }

        Cipher cipher = FACTORY.createDecryptionCrypter("finger");
        if (cipher == null) {
            return false;
        }

        return Objects.equals(CRYPTER.decrypt(cipher, encryptedPin), String.valueOf(true));
    }

    public static void setFingerAuth(boolean bool) {
        Cipher cipher = FACTORY.createEncryptionCrypter("finger");
        if (cipher == null) {
            return;
        }

        String encryptedBool = CRYPTER.encrypt(cipher, String.valueOf(bool));
        PREFS.edit().putString("finger", encryptedBool).apply();
    }
}
