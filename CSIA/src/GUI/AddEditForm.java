package GUI;

import PasswordManager.CryptoUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AddEditForm extends JDialog {
    private JTextField serviceField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextArea notesField;
    private int entryId = -1;
    private String masterPassword;
    private int userId;

    private JCheckBox uppercaseCheck;
    private JCheckBox numbersCheck;
    private JCheckBox symbolsCheck;
    private JSpinner lengthSpinner;

    public AddEditForm(JFrame parent, int userId, String masterPassword, Object[] existingData) {
        super(parent, existingData == null ? "Add New Entry" : "Edit Entry", true);
        this.masterPassword = masterPassword;
        this.userId = userId;

        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 248, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        serviceField = createStyledTextField();
        usernameField = createStyledTextField();
        passwordField = createStyledTextField();
        notesField = createStyledTextArea();

        JButton saveButton = createStyledButton("💾 Save");
        JButton cancelButton = createStyledButton("❌ Cancel");

        panel.add(createFormRow("🔧 Service Name:", serviceField));
        panel.add(createFormRow("👤 Username:", usernameField));
        panel.add(createFormRow("🔐 Password:", passwordField));

        JPanel generatorPanel = new JPanel();
        generatorPanel.setLayout(new GridLayout(3, 2, 10, 10));
        generatorPanel.setBackground(panel.getBackground());
        generatorPanel.setBorder(BorderFactory.createTitledBorder("Password Options"));

        uppercaseCheck = new JCheckBox("Include Uppercase");
        numbersCheck = new JCheckBox("Include Numbers");
        symbolsCheck = new JCheckBox("Include Symbols");
        lengthSpinner = new JSpinner(new SpinnerNumberModel(8, 6, 32, 1));

        generatorPanel.add(uppercaseCheck);
        generatorPanel.add(numbersCheck);
        generatorPanel.add(symbolsCheck);
        generatorPanel.add(new JLabel("Length:"));
        generatorPanel.add(lengthSpinner);

        JButton generateBtn = createStyledButton("🔁 Generate Password");
        generateBtn.addActionListener(e -> {
            boolean useUpper = uppercaseCheck.isSelected();
            boolean useNum = numbersCheck.isSelected();
            boolean useSym = symbolsCheck.isSelected();
            int length = (Integer) lengthSpinner.getValue();

            if (length < 6) {
                JOptionPane.showMessageDialog(this, "⚠️ Password length must be at least 6.");
                return;
            }

            String generated = generatePassword(useUpper, useNum, useSym, length);
            passwordField.setText(generated);
        });

        panel.add(generatorPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(generateBtn);
        panel.add(Box.createVerticalStrut(10));

        panel.add(new JLabel("📝 Notes:"));
        panel.add(new JScrollPane(notesField));
        panel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(panel.getBackground());
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        if (existingData != null) {
            entryId = (int) existingData[0];
            setFields(
                (String) existingData[1],
                (String) existingData[2],
                (String) existingData[3],
                (String) existingData[4]
            );
        }

        saveButton.addActionListener(e -> saveEntry());
        cancelButton.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
    }

    private JPanel createFormRow(String labelText, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(5, 5));
        row.setBackground(new Color(245, 248, 255));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        row.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        return row;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 240), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 240), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return area;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(60, 130, 230));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private String generatePassword(boolean useUppercase, boolean useNumbers, boolean useSymbols, int length) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()-_=+<>?";

        StringBuilder pool = new StringBuilder(lower);
        if (useUppercase) pool.append(upper);
        if (useNumbers) pool.append(numbers);
        if (useSymbols) pool.append(symbols);

        if (pool.length() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select at least one option for password generation.");
            return "";
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randIndex = (int) (Math.random() * pool.length());
            password.append(pool.charAt(randIndex));
        }

        return password.toString();
    }

    private void saveEntry() {
        String service = serviceField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String notes = notesField.getText();

        try {
            String encryptedUsername = CryptoUtils.encrypt(username, masterPassword);
            String encryptedPassword = CryptoUtils.encrypt(password, masterPassword);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password", "root", "12345678")) {
                String query;

                if (entryId == -1) {
                    query = "INSERT INTO password_entries (user_id, service_name, encrypted_username, encrypted_password, notes, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                } else {
                    query = "UPDATE password_entries SET service_name=?, encrypted_username=?, encrypted_password=?, notes=? WHERE id=? AND user_id=?";
                }

                PreparedStatement stmt = conn.prepareStatement(query);
                if (entryId == -1) {
                    stmt.setInt(1, userId);
                    stmt.setString(2, service);
                    stmt.setString(3, encryptedUsername);
                    stmt.setString(4, encryptedPassword);
                    stmt.setString(5, notes);
                } else {
                    stmt.setString(1, service);
                    stmt.setString(2, encryptedUsername);
                    stmt.setString(3, encryptedPassword);
                    stmt.setString(4, notes);
                    stmt.setInt(5, entryId);
                    stmt.setInt(6, userId);
                }

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Entry saved successfully.");
                dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error saving entry: " + ex.getMessage());
        }
    }


    public void setFields(String service, String username, String password, String notes) {
        serviceField.setText(service);
        usernameField.setText(username);
        passwordField.setText(password);
        notesField.setText(notes);
    }
}
