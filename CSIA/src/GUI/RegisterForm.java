package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterForm extends JFrame {
    // JDBC credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/password";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "12345678";

    public RegisterForm() {
        setTitle("📝 Register New User");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        for (JComponent field : new JComponent[]{usernameField, emailField, passwordField, confirmField}) {
            field.setFont(font);
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        }

        JCheckBox showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        showPasswordCheck.setBackground(panel.getBackground());
        showPasswordCheck.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPasswordCheck.addActionListener(e -> {
            char echoChar = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmField.setEchoChar(echoChar);
        });

        JButton registerButton = new JButton("✅ Register");
        registerButton.setFont(font);
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("🔐 Back to Login");
        loginButton.setFont(font);
        loginButton.setBackground(new Color(100, 149, 237));
        loginButton.setForeground(Color.BLACK);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);
        panel.add(showPasswordCheck);
        panel.add(Box.createVerticalStrut(15));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginButton);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                if (isUserExists(conn, username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // ✅ Hash password with BCrypt
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

                String query = "INSERT INTO users (username, hashed_password, email, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, email);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "🎉 User registered successfully!");
                dispose();
                new LoginForm(); // redirect to login

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        add(panel);
        setVisible(true);
    }

    private boolean isUserExists(Connection conn, String username) throws SQLException {
        String query = "SELECT id FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}