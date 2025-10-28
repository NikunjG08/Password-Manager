package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class ForgotPasswordForm extends JFrame {
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheck;
    private JButton updateButton;

    public ForgotPasswordForm() {
        setTitle("Forgot Password");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Reset Your Password");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setForeground(new Color(0, 102, 204));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));

        emailField = createFieldWithLabel(panel, "Email:");
        newPasswordField = createPasswordFieldWithLabel(panel, "New Password:");
        confirmPasswordField = createPasswordFieldWithLabel(panel, "Confirm Password:");

        showPasswordCheck = new JCheckBox("Show Passwords");
        showPasswordCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheck.setBackground(panel.getBackground());
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheck.addActionListener(e -> {
            char echo = showPasswordCheck.isSelected() ? 0 : '•';
            newPasswordField.setEchoChar(echo);
            confirmPasswordField.setEchoChar(echo);
        });
        panel.add(showPasswordCheck);
        panel.add(Box.createVerticalStrut(15));

        updateButton = new JButton("Update Password");
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateButton.setBackground(new Color(0, 153, 76)); // Green color
        updateButton.setForeground(Color.BLACK);
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> updatePassword());
        panel.add(updateButton);

        add(panel);
        setVisible(true);
    }

    private JTextField createFieldWithLabel(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return field;
    }

    private JPasswordField createPasswordFieldWithLabel(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);

        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return field;
    }

    private void updatePassword() {
        String email = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try {
            // Connect to MySQL
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password", "root", "12345678");

            // Check if email exists
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                // Update password
                PreparedStatement updatePs = conn.prepareStatement("UPDATE users SET hashed_password = ? WHERE id = ?");
                updatePs.setString(1, hashedPassword);
                updatePs.setInt(2, userId);
                updatePs.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Password updated successfully.");
                dispose();
                new LoginForm(); // Redirect to login

            } else {
                JOptionPane.showMessageDialog(this, "❌ Email not found in the system.");
            }

            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ForgotPasswordForm::new);
    }
}
