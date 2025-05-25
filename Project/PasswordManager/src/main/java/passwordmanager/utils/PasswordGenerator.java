package passwordmanager.utils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public static String generate(int length, boolean useUpper, boolean useDigits, boolean useSpecial) {
        StringBuilder chars = new StringBuilder(LOWERCASE);
        if(useUpper) chars.append(UPPERCASE);
        if(useDigits) chars.append(DIGITS);
        if(useSpecial) chars.append(SPECIAL);

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
