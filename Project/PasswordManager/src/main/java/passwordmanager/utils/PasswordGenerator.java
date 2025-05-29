package passwordmanager.utils;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "@#&$!^*%";

    public static String generate(int length, boolean useLower, boolean useUpper, boolean useDigits, boolean useSpecial) {
        StringBuilder chars = new StringBuilder();
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        if (useLower) {
            chars.append(LOWERCASE);
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (useUpper) {
            chars.append(UPPERCASE);
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (useDigits) {
            chars.append(DIGITS);
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        if (useSpecial) {
            chars.append(SPECIAL);
            password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        }

        for (int i = password.length(); i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        List<Character> passwordList = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(passwordList);

        return passwordList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

}
