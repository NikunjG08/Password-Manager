package GUI;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private String masterPassword;
    private int userId;
    private String username;
    private PasswordTablePanel tablePanel;

    public DashboardFrame(String username, int userId, String masterPassword) {
        this.username = username;
        this.userId = userId;
        this.masterPassword = masterPassword;

        setTitle("Password Manager - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        applyModernTheme();

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(60, 63, 65));
        JButton addButton = createStyledButton("Add");
        JButton editButton = createStyledButton("Edit");
        JButton deleteButton = createStyledButton("Delete");
        JButton logoutButton = createStyledButton("Logout");

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(Box.createHorizontalStrut(30));
        topPanel.add(logoutButton);

        // Table Panel
        tablePanel = new PasswordTablePanel(userId, masterPassword);
        add(tablePanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Button Actions

        //  Add: Open AddEditForm and refresh table after it's closed
        addButton.addActionListener(e -> {
            AddEditForm addForm = new AddEditForm(this, userId, masterPassword, null);
            addForm.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    tablePanel.refreshTable(); // Refresh after add form is closed
                }
            });
        });

        //  Edit: Refresh after edit
        editButton.addActionListener(e -> {
            int selectedRow = tablePanel.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }
            Object[] rowData = tablePanel.getRowData(selectedRow);
            AddEditForm editForm = new AddEditForm(this, userId, masterPassword, rowData);
            editForm.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    tablePanel.refreshTable(); // Refresh after edit form is closed
                }
            });
        });

        // ✅ Delete: Confirm and refresh after deletion
        deleteButton.addActionListener(e -> {
            int selectedRow = tablePanel.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int entryId = (int) tablePanel.getRowData(selectedRow)[0];
                tablePanel.deleteEntry(entryId);
                tablePanel.refreshTable(); // Refresh after delete
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(75, 110, 175));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(90, 30));
        return button;
    }

    private void applyModernTheme() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Failed to apply Nimbus theme.");
        }
    }
}
