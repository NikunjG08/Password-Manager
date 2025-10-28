package PasswordManager;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGO = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 256;
    private static final String SALT = "a9v5n38s";

    // Replace this with your actual hashed master password
    private static final String MASTER_PASSWORD_HASH = "849f14e1c3f43a1dfbba9a85b5cc110e059a5d4ea02d9e417ac0c9be03c117e5"; // hash of "MySecurePassword123"

    // Encrypts plain data with AES and returns a base64 encoded result
    public static String encrypt(String data, String password) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec keySpec = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypts a base64 encoded string using AES
    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        byte[] encryptedBytes = new byte[combined.length - 16];

        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = getKey(password);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted);
    }

    // Generates a SecretKeySpec from a password using PBKDF2
    private static SecretKeySpec getKey(String password) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(), ITERATIONS, KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGO);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    // Verifies input password against the stored hash
    public static boolean verifyPassword(String inputPassword) {
        return hashPassword(inputPassword).equals(MASTER_PASSWORD_HASH);
    }

    // Hashes a password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
