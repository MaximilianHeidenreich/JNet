public class MyAwesomePacket extends AbstractPacket {

    // Store any custom data as long as its Serializable
    private String message;

    // Add custom constructors to make instantiating simpler
    public MyAwesomeEvent(String message) {
        super();                                // !! Call super to initialize the packet or it won't be transmitted !!
        this.message = message;
    }

    // Add custom logic to improve quality of life
    public String uppercase() {
        return this.message.toUpperCase();
    }

}