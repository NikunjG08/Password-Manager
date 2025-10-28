package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import PasswordManager.CryptoUtils;

import java.awt.*;
import java.sql.*;

public class PasswordTablePanel extends JPanel {
    private int userId;
    private String masterPassword;
    private JTable table;
    private DefaultTableModel model;

    public PasswordTablePanel(int userId, String masterPassword) {
        this.userId = userId;
        this.masterPassword = masterPassword;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Service", "Username", "Password", "Notes", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable direct cell editing
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Hide the ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPasswordEntries();
    }

    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    public Object[] getRowData(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= model.getRowCount()) return null;

        Object[] rowData = new Object[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowData[i] = model.getValueAt(rowIndex, i);
        }
        return rowData;
    }

    public void deleteEntry(int entryId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password", "root", "12345678")) {
            String query = "DELETE FROM password_entries WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, entryId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting entry: " + e.getMessage());
        }
    }

    public void refreshTable() {
        loadPasswordEntries();
    }

    private void loadPasswordEntries() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password", "root", "12345678")) {
            String query = "SELECT id, service_name, encrypted_username, encrypted_password, notes, created_at FROM password_entries WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            model.setRowCount(0); // Clear existing
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("service_name"),
                        decrypt(rs.getString("encrypted_username")),
                        decrypt(rs.getString("encrypted_password")),
                        rs.getString("notes"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private String decrypt(String encrypted) {
        try {
            return CryptoUtils.decrypt(encrypted, masterPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return "[Decryption Failed]";
        }
    }

}
