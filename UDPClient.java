import java.io.*;
import java.net.*;

public class UDPClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            Thread client1 = new Thread(() -> registerClient("Client 1"));
            Thread client2 = new Thread(() -> registerClient("Client 2"));
            Thread client3 = new Thread(() -> registerClient("Client 3"));
            Thread client4 = new Thread(() -> registerClient("Client 4"));

            client1.start();
            client2.start();
            client3.start();
            client4.start();

            client1.join();
            client2.join();
            client3.join();
            client4.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void registerClient(String clientName) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            sendRequest(clientSocket, serverAddress, "register");

            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(clientName + " received response from server:\n" + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(DatagramSocket clientSocket, InetAddress serverAddress, String message) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
        clientSocket.send(sendPacket);
    }
}


