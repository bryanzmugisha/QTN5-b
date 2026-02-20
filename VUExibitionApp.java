import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VUExibitionApp extends JFrame {

    // ── Text Fields ───
    private JTextField txtRegID, txtName, txtFaculty,
                       txtProject, txtContact, txtEmail;

    // ── Database connection details ───
    // (iii) UCanAccess JDBC connection string
    private static final String DB_URL =
        "jdbc:ucanaccess://C:/Databases/VUE_Exhibition.accdb";

    // ── Constructor:
    public VUExibitionApp() {
        setTitle("Victoria University – Innovation & Technology Exhibition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 248, 255));

        // ── Header label
        JLabel header = new JLabel(
            "Student Exhibition Registration", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(0, 51, 102));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // ── Form panel (GridBagLayout for alignment)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 5, 8, 5);
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        // Helper arrays for cleaner field creation
        String[] labels = {
            "Registration ID:",
            "Student Name:",
            "Faculty:",
            "Project Title:",
            "Contact Number:",
            "Email Address:"
        };

        txtRegID   = new JTextField(25);
        txtName    = new JTextField(25);
        txtFaculty = new JTextField(25);
        txtProject = new JTextField(25);
        txtContact = new JTextField(25);
        txtEmail   = new JTextField(25);

        JTextField[] fields = {
            txtRegID, txtName, txtFaculty,
            txtProject, txtContact, txtEmail
        };

        // Add label + field rows
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)),
                new EmptyBorder(4, 6, 4, 6)));
            formPanel.add(fields[i], gbc);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ── Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(245, 248, 255));

        JButton btnRegister = createButton("Register", new Color(0, 102, 51));
        JButton btnClear    = createButton("Clear",    new Color(0, 70, 140));
        JButton btnExit     = createButton("Exit",     new Color(153, 0, 0));

        btnPanel.add(btnRegister);
        btnPanel.add(btnClear);
        btnPanel.add(btnExit);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ── (iv) Register button action ──────────────────────────
        btnRegister.addActionListener(e -> registerStudent());

        // ── Clear button action
        btnClear.addActionListener(e -> clearFields());

        // ── Exit button action
        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });
    }

    // ── Styled button helper ─────────────────────────────────────
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.BLUE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return btn;
    }

    // ── (iii) Database connection method ─────────────────────────
    private Connection getConnection() throws SQLException {
        // UCanAccess driver is loaded automatically from the classpath
        return DriverManager.getConnection(DB_URL);
    }

    // ── (iv) Insert record into Participants table ────────────────
    private void registerStudent() {
        // Retrieve and trim inputs
        String regID   = txtRegID.getText().trim();
        String name    = txtName.getText().trim();
        String faculty = txtFaculty.getText().trim();
        String project = txtProject.getText().trim();
        String contact = txtContact.getText().trim();
        String email   = txtEmail.getText().trim();

        // Basic validation
        if (regID.isEmpty() || name.isEmpty() || faculty.isEmpty() ||
            project.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required. Please complete the form.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Email format check
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Invalid Email", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return;
        }

        // SQL INSERT with PreparedStatement (prevents SQL injection)
        String sql = "INSERT INTO Participants " +
                     "(RegistrationID, StudentName, Faculty, " +
                     " ProjectTitle, ContactNumber, EmailAddress) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, regID);
            pstmt.setString(2, name);
            pstmt.setString(3, faculty);
            pstmt.setString(4, project);
            pstmt.setString(5, contact);
            pstmt.setString(6, email);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Student registered successfully!\nRegistration ID: " + regID,
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();

        } catch (SQLException ex) {
            // Duplicate primary key
            if (ex.getMessage().toLowerCase().contains("duplicate")) {
                JOptionPane.showMessageDialog(this,
                    "Registration ID already exists. Please use a unique ID.",
                    "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Clear all fields ─────────────────────────────────────────
    private void clearFields() {
        txtRegID.setText("");
        txtName.setText("");
        txtFaculty.setText("");
        txtProject.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtRegID.requestFocus();
    }

    // ── Entry point ──────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new VUExibitionApp().setVisible(true);
        });
    }
}