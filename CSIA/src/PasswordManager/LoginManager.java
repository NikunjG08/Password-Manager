package PasswordManager;

import GUI.AddEditForm;

import javax.swing.*;
import java.sql.*;

public class LoginManager {

    public static void saveLogin(String site, String user, String pass, String notes, String masterPassword) {
        try (Connection conn = DBConnection.getConnection()) {
            String encryptedPass = CryptoUtils.encrypt(pass, masterPassword);
            String encryptedNotes = CryptoUtils.encrypt(notes, masterPassword);

            String query = "INSERT INTO logins (site, username, password, notes) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, site);
            stmt.setString(2, user);
            stmt.setString(3, encryptedPass);
            stmt.setString(4, encryptedNotes);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error saving login: " + e.getMessage());
        }
    }

    public static void updateLogin(int id, String site, String user, String pass, String notes, String masterPassword) {
        try (Connection conn = DBConnection.getConnection()) {
            String encryptedPass = CryptoUtils.encrypt(pass, masterPassword);
            String encryptedNotes = CryptoUtils.encrypt(notes, masterPassword);

            String query = "UPDATE logins SET site=?, username=?, password=?, notes=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, site);
            stmt.setString(2, user);
            stmt.setString(3, encryptedPass);
            stmt.setString(4, encryptedNotes);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating login: " + e.getMessage());
        }
    }

    public static void deleteLogin(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM logins WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting login: " + e.getMessage());
        }
    }

    public static void populateFields(AddEditForm form, int id, String masterPassword) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM logins WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String site = rs.getString("site");
                String user = rs.getString("username");
                String pass = CryptoUtils.decrypt(rs.getString("password"), masterPassword);
                String notes = CryptoUtils.decrypt(rs.getString("notes"), masterPassword);
                form.setFields(site, user, pass, notes);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading login: " + e.getMessage());
        }
    }
}
