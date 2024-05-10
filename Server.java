import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private boolean running = false;
    private Thread serverThread; 
    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private JButton exitButton;
    private JLabel portLabel;
    private JTextField portField;
    private JTextArea statusArea;

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    @Override
    public void run() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        portLabel = new JLabel("Server Port:");
        portLabel.setBounds(98, 20, 100, 16);
        frame.getContentPane().add(portLabel);

        portField = new JTextField();
        portField.setBounds(180, 15, 130, 26);
        frame.getContentPane().add(portField);
        portField.setColumns(10);

        startButton = new JButton("Start Server");
        startButton.setBounds(30, 220, 117, 29);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        frame.getContentPane().add(startButton);

        stopButton = new JButton("Stop Server");
        stopButton.setBounds(160, 220, 117, 29);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
        frame.getContentPane().add(stopButton);

        exitButton = new JButton("Exit Server");
        exitButton.setBounds(290, 220, 117, 29);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitServer();
            }
        });
        frame.getContentPane().add(exitButton);

        statusArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBounds(30, 60, 400, 150);
        frame.getContentPane().add(scrollPane);
        
        frame.setVisible(true);
    }

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    int port = Integer.parseInt(portField.getText());
                    serverSocket = new ServerSocket(port);
                    running = true;
                    statusArea.append("Server started on port " + port + "\n");

                    while (running) {
                        Socket clientSocket = serverSocket.accept();
                        statusArea.append("Client connected: " + clientSocket + "\n");

                        handleClient(clientSocket);
                    }
                } catch (NumberFormatException ex) {
                    statusArea.append("Invalid port number! Please enter a valid integer.\n");               
                }
                return null;
            }
        };
        worker.execute();
        serverThread = new Thread(worker); 
        serverThread.start();
    }

    private void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                running = false; 
                serverSocket.close();
                statusArea.append("Server stopped.\n");

                Thread.sleep(100); 

                if (serverThread != null && serverThread.isAlive()) {
                    serverThread.interrupt();
                }
            } else {
                statusArea.append("Server is not running.\n");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void exitServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private void handleClient(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
    
            while (running) {
                int number = in.readInt();
    
                if (number == -1) {
                    break; 
                }

                long result = calculateFactorial(number);

                out.writeLong(result);
                out.flush();
            }
 
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private long calculateFactorial(int n) {
        if (n == 0)
            return 1;
        else
            return n * calculateFactorial(n - 1);
    }
}





