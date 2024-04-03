import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static final int PORT = 9876;
    private static final int BUFFER_SIZE = 1024;
    private static List<SocketAddress> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket server_Socket = new DatagramSocket(PORT);
            byte[] receive_Data = new byte[BUFFER_SIZE];

            System.out.println("Server started...");

            // Thread to handle console input
            Thread consoleInputThread = new Thread(() -> {
                try {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        String console_Message = consoleReader.readLine();
                        if (console_Message.equalsIgnoreCase("exit")) {
                            server_Socket.close();
                            break; 
                        }
                        sendToAllClients(server_Socket, console_Message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            consoleInputThread.start();

            while (true) {
                DatagramPacket receive_Packet = new DatagramPacket(receive_Data, receive_Data.length);
                server_Socket.receive(receive_Packet);

                String message = new String(receive_Packet.getData(), 0, receive_Packet.getLength());
                InetAddress client_Address = receive_Packet.getAddress();
                int clientPort = receive_Packet.getPort();
                SocketAddress client_Socket_Address = new InetSocketAddress(client_Address, clientPort);

                System.out.println("Client (" + client_Address + ":" + clientPort + "): " + message);

                if (message.equalsIgnoreCase("Goodbye")) {
                    clients.remove(client_Socket_Address);
                    System.out.println("Client (" + client_Address + ":" + clientPort + ") disconnected.");
                } else if (!clients.contains(client_Socket_Address)) {
                    clients.add(client_Socket_Address);
                }

                
                sendToAllClients(server_Socket, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendToAllClients(DatagramSocket server_Socket, String message) throws IOException {
        byte[] sendData = message.getBytes();
        for (SocketAddress client : clients) {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client);
            server_Socket.send(sendPacket);
        }
    }
}
