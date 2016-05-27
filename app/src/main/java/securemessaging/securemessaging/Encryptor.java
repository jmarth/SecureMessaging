package securemessaging.securemessaging;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;

/**
 * Created by marth on 4/23/2016.
 */
public class Encryptor {
    public Encryptor() {
    }

    public static String encrypt(String key, String message) {
        try {
            // encrypt the text
            Cipher cipher = Cipher.getInstance("AES");
            Key aesKey = new SecretKeySpec(key.getBytes("UTF8"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes("UTF8"));
            StringBuilder sb = new StringBuilder();
            for (byte b: encrypted) {
                sb.append((char)b);
            }

            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String encrypted) {
        try {
            byte[] bb = new byte[encrypted.length()];
            for (int i=0; i<encrypted.length(); i++) {
                bb[i] = (byte) encrypted.charAt(i);
            }
            Cipher cipher = Cipher.getInstance("AES");
            Key aesKey = new SecretKeySpec(key.getBytes("UTF8"), "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(bb));
            return decrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
