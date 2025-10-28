package GUI;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



public class DelayedLogin extends JFrame {
    private ImageIcon gifImage;

    public DelayedLogin() {
        // Create a JPanel to hold the GIF image
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        // Load the GIF image
        URL gifImageURL = getClass().getResource("/images/intro.gif");
        if (gifImageURL != null) {
            gifImage = new ImageIcon(gifImageURL);
        } else {
            System.err.println("GIF image not found!");
        }

        // Create a JLabel to display the GIF image
        JLabel gifLabel = new JLabel(gifImage);

        // Add the GIF label to the center of the content pane
        contentPane.add(gifLabel, BorderLayout.CENTER);

        // Configure the main JFrame
        setTitle("Loading...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack(); // Pack the frame to fit the content
        setLocationRelativeTo(null);
        setVisible(true);

        // Create a timer to delay opening the login page for 5 seconds (5000 milliseconds)
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // After the delay, close this frame and open the login page
                dispose(); // Close this frame
                openLoginPage(); // Open the login page
            }
        });

        // Start the timer
        timer.setRepeats(false); // Ensure it only runs once
        timer.start();
    }

    private void openLoginPage() {
        // Create and show the login page
        SwingUtilities.invokeLater(() -> {
        	RegisterForm loginPage = new RegisterForm();
            loginPage.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DelayedLogin();
        });
    }
}

