package passwordmanager.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHasher {
    public static final int ITERATIONS = 10000;
    public static final int KEY_LENGTH = 256;
    public static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt){
        try{
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hast = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hast);
        } catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String storedHash) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(storedHash);
    }
}