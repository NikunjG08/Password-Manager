package PasswordManager;

public class StrengthChecker {
    public static String getStrengthLevel(String password) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+=<>?].*")) score++;

        return switch (score) {
            case 5 -> "Very Strong";
            case 4 -> "Strong";
            case 3 -> "Medium";
            case 2 -> "Weak";
            default -> "Very Weak";
        };
    }
}
