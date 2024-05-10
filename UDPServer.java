import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServer {
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;
    private static Map<String, Integer> clients = new HashMap<>();

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("UDP Server is running on port " + SERVER_PORT);

            while (true) {
                byte[] receiveData = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received message from " + clientAddress.getHostAddress() + ":" + clientPort + ": " + message);

                if (message.equals("register")) {
                    clients.put(clientAddress.getHostAddress(), clientPort);
                    sendRegisteredClients(serverSocket, clientAddress, clientPort);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendRegisteredClients(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) throws IOException {
        StringBuilder response = new StringBuilder("Registered clients:\n");
        for (Map.Entry<String, Integer> entry : clients.entrySet()) {
            response.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }
        byte[] sendData = response.toString().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        serverSocket.send(sendPacket);
    }
}


