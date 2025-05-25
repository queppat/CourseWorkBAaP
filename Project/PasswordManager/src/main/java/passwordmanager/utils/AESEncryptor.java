    package passwordmanager.utils;

    import javax.crypto.Cipher;
    import javax.crypto.SecretKey;
    import javax.crypto.SecretKeyFactory;
    import javax.crypto.spec.IvParameterSpec;
    import javax.crypto.spec.PBEKeySpec;
    import javax.crypto.spec.SecretKeySpec;
    import java.security.SecureRandom;
    import java.security.spec.KeySpec;
    import java.util.Base64;

    public class AESEncryptor {
        public static final String ALGORITHM = "AES/CBC/PKCS5Padding";
        public static final int IV_LENGTH = 16;

        private static final int KEY_SIZE = 256;
        private static final int ITERATIONS = 65536;

        public static String encrypt(String data, String masterPassword, String salt) throws Exception {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKey secretKey = generateKey(masterPassword,salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes());

            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(combined);
        }

        public static String decrypt(String encryptedData, String masterPassword, String salt) throws Exception {
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[combined.length - IV_LENGTH];

            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            SecretKey secretKey = generateKey(masterPassword,salt);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted);
        }

        private static SecretKey generateKey(String password, String salt) throws Exception {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), ITERATIONS, KEY_SIZE);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        }
    }
