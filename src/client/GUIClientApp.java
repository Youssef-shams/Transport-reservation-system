package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GUIClientApp extends JFrame {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8080;

    // --- Modern Dark Theme Palette ---
    private final Color BACKGROUND_COLOR = new Color(30, 33, 36);      // Deep Dark Grey
    private final Color COMPONENT_BG = new Color(43, 45, 49);          // Slightly lighter for text areas
    private final Color TEXT_LIGHT = new Color(219, 222, 225);         // Soft Off-White
    private final Color PRIMARY_COLOR = new Color(88, 101, 242);       // Modern Blue
    private final Color SUCCESS_COLOR = new Color(35, 165, 89);        // Emerald Green
    private final Color DANGER_COLOR = new Color(218, 55, 60);         // Muted Crimson

    // GUI Components
    private JTextArea displayArea;
    private JTextField seatInputField;
    private JButton viewButton;
    private JButton bookButton;
    private JButton quitButton;

    // Network Components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public GUIClientApp() {

        // 1. Window Configuration
        setTitle("Transport Reservation Terminal");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Root panel with padding
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        rootPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(rootPanel);

        // 2. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Transport Seat Reservation", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_LIGHT); // Updated to light text
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 3. Central Output Display Area (Monospaced for the Grid)
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.BOLD, 14));
        displayArea.setBackground(COMPONENT_BG);     // Dark background for text area
        displayArea.setForeground(TEXT_LIGHT);       // Light text for readability
        displayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setViewportBorder(null);
        // --- ADD THESE TWO LINES ---
        // This forces both the vertical and horizontal scrollbars to hide permanently
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // ---------------------------
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Dark border
        scrollPane.getViewport().setBackground(COMPONENT_BG);
        rootPanel.add(scrollPane, BorderLayout.CENTER);

        // 4. Control Panel (Input Form + Actions Grid)
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);

        // Row 1: Styled Entry Field
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inputPanel.setBackground(BACKGROUND_COLOR);
        JLabel inputLabel = new JLabel("Enter Seat Number:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputLabel.setForeground(TEXT_LIGHT); // Updated to light text

        seatInputField = new JTextField(8);
        seatInputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatInputField.setHorizontalAlignment(JTextField.CENTER);
        seatInputField.setBackground(COMPONENT_BG); // Dark background
        seatInputField.setForeground(TEXT_LIGHT);   // Light text
        seatInputField.setCaretColor(TEXT_LIGHT);   // Ensure cursor is visible
        seatInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 68), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        inputPanel.add(inputLabel);
        inputPanel.add(seatInputField);
        controlPanel.add(inputPanel);

        // Row 2: Modern Flat Action Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        viewButton = createStyledButton("View Seats", PRIMARY_COLOR);
        bookButton = createStyledButton("Book Seat", SUCCESS_COLOR);
        quitButton = createStyledButton("Exit Terminal", DANGER_COLOR);

        buttonPanel.add(viewButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(quitButton);
        controlPanel.add(buttonPanel);

        rootPanel.add(controlPanel, BorderLayout.SOUTH);

        // 5. Establish socket backend pipeline & wire listeners
        connectToServer();
        setupActionListeners();
    }

    // Helper method to keep button styling clean and standardized
    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // THE FIX: Forces the OS to respect the custom background colors
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false); // Disables the native OS 3D border effect

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return btn;
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String welcomeMsg = in.readLine();
            displayArea.append("[SYSTEM]: Connected cleanly to system infrastructure.\n");
            displayArea.append("[SERVER]: " + welcomeMsg + "\n\n");

        } catch (IOException e) {
            displayArea.append("[CRITICAL ERROR]: Could not communicate with server core.\n");
            displayArea.append("Details: " + e.getMessage() + "\n");
            disableControls();
        }
    }

    private void setupActionListeners() {
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out.println("VIEW");
                    String response = in.readLine();

                    if (response != null && response.startsWith("--- Seating Chart")) {
                        displayArea.setText("");
                        displayArea.append(response + "\n\n");
                        String seatLine;
                        while (!(seatLine = in.readLine()).equals("END_OF_LIST")) {
                            displayArea.append(seatLine + "\n");
                        }
                    } else {
                        displayArea.append(response + "\n");
                    }
                } catch (IOException ex) {
                    handleDisconnect(ex);
                }
            }
        });

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seatNumber = seatInputField.getText().trim().toUpperCase();
                if (seatNumber.isEmpty()) {
                    // Update JOptionPane to match dark theme styling roughly (JOptionPane usually uses system defaults)
                    JOptionPane.showMessageDialog(GUIClientApp.this,
                            "Please input a valid target seat character mapping.",
                            "Data Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    out.println("BOOK " + seatNumber);
                    String response = in.readLine();
                    displayArea.append("\nClient Action -> Requested target allocation: " + seatNumber + "\n");
                    displayArea.append("Server Feedback -> " + response + "\n");
                    seatInputField.setText("");
                } catch (IOException ex) {
                    handleDisconnect(ex);
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (out != null) out.println("QUIT");
                } catch (Exception ex) {
                    // Ignore if streams are dropped
                } finally {
                    closeConnection();
                    System.exit(0);
                }
            }
        });
    }

    private void handleDisconnect(IOException ex) {
        displayArea.append("\n[CRITICAL ERROR]: Pipeline dropped unexpectedly.\n" + ex.getMessage() + "\n");
        disableControls();
        closeConnection();
    }

    private void disableControls() {
        viewButton.setEnabled(false);
        bookButton.setEnabled(false);
        seatInputField.setEnabled(false);
        viewButton.setBackground(new Color(60, 63, 68)); // Darker grey for disabled state
        bookButton.setBackground(new Color(60, 63, 68));
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIClientApp().setVisible(true);
            }
        });
    }
}