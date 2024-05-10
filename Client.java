import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame frame;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton exitButton;
    private JLabel numberLabel;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JTextField numberField;
    private JTextField addressField;
    private JTextField portField;
    private JTextArea resultArea;

    public static void main(String[] args) {
        Client client = new Client();
        client.initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        numberLabel = new JLabel("Number:");
        numberLabel.setBounds(30, 20, 70, 16);
        frame.getContentPane().add(numberLabel);

        numberField = new JTextField();
        numberField.setBounds(110, 15, 130, 26);
        frame.getContentPane().add(numberField);
        numberField.setColumns(10);

        addressLabel = new JLabel("Server Address:");
        addressLabel.setBounds(250, 20, 100, 16);
        frame.getContentPane().add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(350, 15, 80, 26);
        frame.getContentPane().add(addressField);
        addressField.setColumns(10);

        portLabel = new JLabel("Server Port:");
        portLabel.setBounds(30, 50, 70, 16);
        frame.getContentPane().add(portLabel);

        portField = new JTextField();
        portField.setBounds(110, 45, 80, 26);
        frame.getContentPane().add(portField);
        portField.setColumns(10);

        calculateButton = new JButton("Calculate");
        calculateButton.setBounds(30, 220, 117, 29);
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
        frame.getContentPane().add(calculateButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(160, 220, 117, 29);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        frame.getContentPane().add(clearButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(290, 220, 117, 29);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        frame.getContentPane().add(exitButton);

        resultArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(30, 90, 400, 100);
        frame.getContentPane().add(scrollPane);

        frame.setVisible(true);
    }

    private void calculate() {
        try {
            String serverAddress = addressField.getText();
            int serverPort = Integer.parseInt(portField.getText());
            int number = Integer.parseInt(numberField.getText());

            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            long startTime = System.nanoTime();
            out.writeInt(number);
            out.flush();
            long result = in.readLong();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;

            resultArea.append("Result: " + result + "\n");
            resultArea.append("Time taken: " + elapsedTime + " ns\n");

            out.writeInt(-1); 
            out.flush();
        } catch (NumberFormatException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void clear() {
        resultArea.setText("");
        numberField.setText("");
    }

    private void exit() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}




