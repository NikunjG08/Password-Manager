package PasswordManager;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%&*?";

    public static String generate(int length, boolean useUpper, boolean useNumbers, boolean useSymbols) {
        if (length < 6) length = 6;
        String charPool = LOWERCASE;
        if (useUpper) charPool += UPPERCASE;
        if (useNumbers) charPool += NUMBERS;
        if (useSymbols) charPool += SYMBOLS;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(charPool.charAt(random.nextInt(charPool.length())));
        }
        return password.toString();
    }
}
