package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JLabel errorLabel;
    private String masterPassword;
    private int userId;

    public LoginForm() {
        setTitle("🔐 Password Manager - Login");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(240, 248, 255)); // Soft light blue background

        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setFont(font);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        passwordField = new JPasswordField();
        passwordField.setFont(font);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        showPasswordCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheck.setBackground(panel.getBackground());
        showPasswordCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
        });

        JButton loginButton = new JButton("🔐 Login");
        loginButton.setFont(font);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        loginButton.setForeground(Color.BLACK);
        loginButton.addActionListener(e -> attemptLogin());

        JButton registerButton = new JButton("📝 Register");
        registerButton.setFont(font);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(e -> new RegisterForm());

        JButton forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(30, 144, 255)); // Dodger Blue
        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordButton.addActionListener(e -> new ForgotPasswordForm());

        errorLabel = new JLabel(" ", JLabel.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(showPasswordCheck);
        panel.add(Box.createVerticalStrut(15));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(forgotPasswordButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(errorLabel);

        add(panel);
        setVisible(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password", "root", "12345678");

            PreparedStatement ps = conn.prepareStatement("SELECT id, hashed_password FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("hashed_password");

                if (BCrypt.checkpw(password, storedHash)) {
                
                    this.masterPassword = password;
                    this.userId = rs.getInt("id");

                    JOptionPane.showMessageDialog(this, "Welcome, " + username + "!");
                    dispose();
                    new DashboardFrame(username, userId, masterPassword);
                } else {
                    errorLabel.setText("Invalid username or password.");
                }
            } else {
                errorLabel.setText("Invalid username or password.");
            }

            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Database error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
