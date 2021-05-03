public class SimpleClient {
    public static void main(String[] args) throws IOException {

        // Create a new client
        Client client = new Client();

        // Add handlers for your packets -> These will get executed once a packet of amtching type is received
        client.addPacketHandler(MyAwesomePacket.class, (packet, connection) -> {
            System.out.println("Client | Got MyAwesomePacket message: " + packet.message);

            // Do something... (feel free to use your custom logic from inside the packet's class)
            System.out.println("Do some work on client... " + packet.uppercase());

        });

        // Connect to a server
        client.connect("127.0.0.1", 25566);

        // Optionally set the name of the client
        client.setNameRemote("Client1");

        // Create a packet
        MyAwesomePacket myPacket = new MyAwesomePacket("Hello from client :)");
        myPacket.setTimout(5, TimeUnit.SECONDS);                                   // If a packet does not get handled & no callback is executed after 5 seconds it will fail!

        // Send a packet and execute a callback on response from server
        client.sendThen(myPacket)
                .thenAccept((packet) -> {
                    MyAwesomePacket response = (MyAwesomePacket) packet;                // Here you'll need a typecast because the server could respond with any packet type!
                    // To be safe use "if (packet instanceof xxx)" first
                    System.out.println("CLIENT | Got response from server: " + response.message);
                })
                .exceptionally(e -> {           // Catch possible exceptions like PacketTimeoutException
                    e.printStackTrace();        // You probably want to check the exception type and handle it accordingly
                    return null;                // Don't forget this, it is needed due to the way CompletableFutures work
                });

    }
}