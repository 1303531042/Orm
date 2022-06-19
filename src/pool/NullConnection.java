package pool;

public class NullConnection extends Exception {
    public NullConnection() {
    }

    public NullConnection(String mess) {
        super(mess);
    }
}
