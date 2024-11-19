import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreams extends JFrame {
    private JTextArea textAreaOriginal; // For original file content
    private JTextArea textAreaFiltered; // For filtered file content
    private JTextField searchField; // Input for the search string
    private File selectedFile; // Stores the selected file

    public DataStreams() {
        setTitle("Data Streams Search Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Top Panel for buttons and search field
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton btnLoadFile = new JButton("Load File");
        JButton btnSearch = new JButton("Search");
        JButton btnQuit = new JButton("Quit");
        searchField = new JTextField(20);
        JLabel searchLabel = new JLabel("Search:");

        topPanel.add(btnLoadFile);
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(btnSearch);
        topPanel.add(btnQuit);

        // Center Panel for text areas (Original and Filtered)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        textAreaOriginal = new JTextArea();
        textAreaFiltered = new JTextArea();
        JScrollPane scrollPaneOriginal = new JScrollPane(textAreaOriginal);
        JScrollPane scrollPaneFiltered = new JScrollPane(textAreaFiltered);

        textAreaOriginal.setEditable(false);
        textAreaFiltered.setEditable(false);

        centerPanel.add(scrollPaneOriginal);
        centerPanel.add(scrollPaneFiltered);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Button Actions
        btnLoadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFileAction();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchFileAction();
            }
        });

        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the application
            }
        });
    }

    private void loadFileAction() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getAbsolutePath()))) {
                String content = lines.collect(Collectors.joining("\n"));
                textAreaOriginal.setText(content); // Display file content in the left text area
                textAreaFiltered.setText(""); // Clear the filtered text area
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFileAction() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please load a file before searching.",
                    "No File Loaded", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String searchString = searchField.getText().trim();
        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search string.",
                    "Empty Search String", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getAbsolutePath()))) {
            List<String> filteredLines = lines
                    .filter(line -> line.toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());
            textAreaFiltered.setText(String.join("\n", filteredLines)); // Display filtered lines
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error searching file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreams app = new DataStreams();
            app.setVisible(true);
        });
    }
}
