import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HostelManagementSystemGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/swapnil?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "system";
    private Connection connection;

    public HostelManagementSystemGUI() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setTitle("Hostel Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField(10);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(10);

        JLabel branchLabel = new JLabel("Branch of Engineering:");
        JTextField branchField = new JTextField(10);

        JLabel yearLabel = new JLabel("Year of Engineering:");
        JTextField yearField = new JTextField(10);

        JButton addButton = new JButton("Add Person");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String branch = branchField.getText();
                String year = yearField.getText();

                // Check if the name is provided (assuming that it's a valid condition for adding a person)
                if (!name.isEmpty()) {
                    int roomNumber = Integer.parseInt(roomNumberField.getText());
                    try {
                        // Insert data into the database for the specified room
                        PreparedStatement statement = connection.prepareStatement("INSERT INTO hostel_data (is_room, room_number, name, phone, branch, year) VALUES (0, ?, ?, ?, ?, ?)");
                        statement.setInt(1, roomNumber);
                        statement.setString(2, name);
                        statement.setString(3, phone);
                        statement.setString(4, branch);
                        statement.setString(5, year);
                        statement.executeUpdate();

                        // Clear input fields
                        roomNumberField.setText("");
                        nameField.setText("");
                        phoneField.setText("");
                        branchField.setText("");
                        yearField.setText("");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton removeButton = new JButton("Remove Person");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int roomNumber = Integer.parseInt(roomNumberField.getText());
                try {
                    // Remove data from the database for the specified room
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM hostel_data WHERE is_room = 0 AND room_number = ?");
                    statement.setInt(1, roomNumber);
                    statement.executeUpdate();

                    // Clear input fields
                    roomNumberField.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton showRoommatesDetailsButton = new JButton("Show Roommates Details");
        showRoommatesDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String roomNumberText = roomNumberField.getText();
                if (!roomNumberText.isEmpty()) {
                    int roomNumber = Integer.parseInt(roomNumberText);
                    try {
                        PreparedStatement statement = connection.prepareStatement("SELECT name, phone, branch, year FROM hostel_data WHERE is_room = 0 AND room_number = ?");
                        statement.setInt(1, roomNumber);
                        ResultSet resultSet = statement.executeQuery();

                        StringBuilder details = new StringBuilder();

                        while (resultSet.next()) {
                            String name = resultSet.getString("name");
                            String phone = resultSet.getString("phone");
                            String branch = resultSet.getString("branch");
                            String year = resultSet.getString("year");

                            details.append("Name: ").append(name).append("\nPhone: ").append(phone).append("\nBranch: ").append(branch).append("\nYear: ").append(year).append("\n\n");
                        }

                        if (details.length() > 0) {
                            JOptionPane.showMessageDialog(null, details.toString(), "Roommates Details", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Room is empty", "Roommates Details", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred while fetching roommates' details.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Room Number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(roomNumberLabel);
        add(roomNumberField);
        add(nameLabel);
        add(nameField);
        add(phoneLabel);
        add(phoneField);
        add(branchLabel);
        add(branchField);
        add(yearLabel);
        add(yearField);
        add(addButton);
        add(removeButton);
        add(showRoommatesDetailsButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL JDBC driver
                new HostelManagementSystemGUI();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
