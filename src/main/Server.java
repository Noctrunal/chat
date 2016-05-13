package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Connection connection : connectionMap.values()) {
                connection.send(message);
            }
        }
        catch (IOException e) {
            ConsoleHelper.writeMessage("Send message failed...");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String userName = null;
            try (Connection connection = new Connection(socket)) {
                ConsoleHelper.writeMessage(String.format("Connection success with remote address: %s", connection.getRemoteSocketAddress()));
                userName = serverHandshake(connection);
                Server.sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
            }
            catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Connection error. Try again later...");
            }
            connectionMap.remove(userName);
            Server.sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            ConsoleHelper.writeMessage("Connection to server closed...");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if (answer.getType() == MessageType.USER_NAME) {
                    String userName = answer.getData();
                    if (userName != null && !userName.isEmpty()) if (!connectionMap.containsKey(userName)) {
                        connectionMap.put(userName, connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        return userName;
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                String name = pair.getKey();
                if (!name.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message clientMessage = connection.receive();
                if (clientMessage.getType() == MessageType.TEXT) {
                    String text = clientMessage.getData();
                    String messagePattern = "%s: %s";
                    String result = String.format(messagePattern, userName, text);
                    Server.sendBroadcastMessage(new Message(MessageType.TEXT, result));
                } else {
                    ConsoleHelper.writeMessage("Wrong text message...Try again...");
                }
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(ConsoleHelper.readString()))) {
            ConsoleHelper.writeMessage("Server startup...");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        }
        catch (IOException e) {
            ConsoleHelper.writeMessage("Error to connect...");
        }
    }
}
