public class SimpleServer {
    public static void main(String[] args) throws IOException {

        // Create a server listening on 127.0.0.1:25566
        Server server = new Server("127.0.0.1", 25566);

        // Add handlers for your packets -> These will get executed once a packet of amtching type is received
        server.addPacketHandler(MyAwesomePacket.class, (packet, connection) -> {
            System.out.println("Server | Got MyAwesomePacket message: " + packet.message);

            // Do something... (feel free to use your custom logic from inside the packet's class)
            System.out.println("Do some work on server... " + packet.uppercase());

            // Send back some response data (The client can handle this packet if he used "sendThen")
            // Note: You could send back any type of AbstractPacket. Conventionally you use the same type or use a "xxxRequestPacket" & "xxxResposnePacket" structure
            try {
                MyAwesomePacket response = new MyAwesomePacket("Hey from server! I got: " + packet.message);
                connection.sendRaw(response);
            } catch (IOException exception) {
                exception.printStackTrace();        // You probably want to handle this in another way!
            }
        });

        // Start the server to accept new connections
        server.start();

    }
}