import java.io.*;
import java.net.*;

public class UDPClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            DatagramSocket client_Socket = new DatagramSocket();
            InetAddress server_Address = InetAddress.getByName(SERVER_IP);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Client Started...");
            System.out.println("Type 'Goodbye' to exit.");

            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] receiveData = new byte[BUFFER_SIZE];
                        DatagramPacket receive_Packet = new DatagramPacket(receiveData, receiveData.length);
                        client_Socket.receive(receive_Packet);

                        String receivedMessage = new String(receive_Packet.getData(), 0, receive_Packet.getLength());
                        System.out.println("Server: " + receivedMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // Sending messages to server
            while (true) {
                System.out.print("You: ");
                String message = userInput.readLine();
                byte[] sendData = message.getBytes();

                DatagramPacket send_Packet = new DatagramPacket(sendData, sendData.length, server_Address, SERVER_PORT);
                client_Socket.send(send_Packet);

                if (message.equalsIgnoreCase("Goodbye")) {
                    break;
                }
            }

            client_Socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
