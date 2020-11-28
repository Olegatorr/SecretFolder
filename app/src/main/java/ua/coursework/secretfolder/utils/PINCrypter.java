package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import ua.coursework.secretfolder.utils.md5Calculator;

import javax.crypto.Cipher;

import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.impl.Base64CipherCrypter;
import co.infinum.goldfinger.crypto.impl.UnlockedAesCipherFactory;
import ua.coursework.secretfolder.BuildConfig;

@RequiresApi(Build.VERSION_CODES.M)
public class PINCrypter {

    private static final md5Calculator md5 = new md5Calculator();
    private static final String PREF_FILE = BuildConfig.APPLICATION_ID.replace(".", "_");
    private static SharedPreferences PREFS;
    private static CipherCrypter CRYPTER;
    private static CipherFactory FACTORY;

    @Nullable
    public static String getPin() {
        String encryptedPin = PREFS.getString("MmzD5LnDLfnhu8Q8", "");
        if ("".equals(encryptedPin)) {
            return "";
        }

        Cipher cipher = FACTORY.createDecryptionCrypter("MmzD5LnDLfnhu8Q8");
        if (cipher == null) {
            return "";
        }

        String decrypted = CRYPTER.decrypt(cipher, encryptedPin);

        return decrypted;
    }

    public static void setPin(String pin) {
        Cipher cipher = FACTORY.createEncryptionCrypter("MmzD5LnDLfnhu8Q8");
        if (cipher == null) {
            return;
        }

        String passMD5 = md5.md5Apache(pin);
        String encryptedPin = CRYPTER.encrypt(cipher, passMD5);
        PREFS.edit().putString("MmzD5LnDLfnhu8Q8", encryptedPin).apply();

    }

    public static void init(Context context) {
        PREFS = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        CRYPTER = new Base64CipherCrypter();
        FACTORY = new UnlockedAesCipherFactory(context);
    }

    public static boolean getFingerAuth() {
        String encryptedPin = PREFS.getString("RFu49REaA8EUVx2v", "");
        if ("".equals(encryptedPin)) {
            return false;
        }

        Cipher cipher = FACTORY.createDecryptionCrypter("RFu49REaA8EUVx2v");
        if (cipher == null) {
            return false;
        }

        if (CRYPTER.decrypt(cipher, encryptedPin).equals(String.valueOf(true))){
            return true;
        }else{
            return false;
        }
    }

    public static void setFingerAuth(boolean bool) {
        Cipher cipher = FACTORY.createEncryptionCrypter("RFu49REaA8EUVx2v");
        if (cipher == null) {
            return;
        }

        String encryptedBool = CRYPTER.encrypt(cipher, String.valueOf(bool));
        PREFS.edit().putString("RFu49REaA8EUVx2v", encryptedBool).apply();
    }
}
