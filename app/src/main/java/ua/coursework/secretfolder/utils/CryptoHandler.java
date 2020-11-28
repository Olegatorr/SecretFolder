package ua.coursework.secretfolder.utils;

import android.content.Context;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHandler {

    Cipher cipher;
    md5Calculator md5 = new md5Calculator();

    public byte[] encrypt(Context context, String data){

        // AES = симметричный алгоритм шафрования
        // CBC = режим алгоритма AES
        // PKCS5Padding = режим обработки последних байт данных
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        String stringKey = md5.md5Apache(PINCrypter.getPin());
        byte[] byteKey16 = Arrays.copyOfRange(stringKey.getBytes(), 0, 16);

        SecretKeySpec key = new SecretKeySpec(
                byteKey16,
                "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(byteKey16);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] preparedData = data.getBytes();
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(preparedData);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public byte[] encrypt(Context context, byte[] data){

        // AES = симметричный алгоритм шафрования
        // CBC = режим алгоритма AES
        // PKCS5Padding = режим обработки последних байт данных
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        String stringKey = PreferencesHandler.getValue(context, "PIN", null);
        byte[] byteKey16 = Arrays.copyOfRange(stringKey.getBytes(), 0, 16);

        SecretKeySpec key = new SecretKeySpec(
                byteKey16,
                "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(byteKey16);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String decrypt(Context context, byte[] data){

        // AES = симметричный алгоритм шафрования
        // CBC = режим алгоритма AES
        // PKCS5Padding = режим обработки последних байт данных
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        String stringKey = md5.md5Apache(PINCrypter.getPin());
        byte[] byteKey16 = Arrays.copyOfRange(stringKey.getBytes(), 0, 16);

        SecretKeySpec key = new SecretKeySpec(
                byteKey16,
                "AES");
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] preparedData = data;
        byte[] cipherText = new byte[0];

        try {
            cipherText = cipher.doFinal(preparedData);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String test = new String(cipherText);;

        return test;
    }
}
