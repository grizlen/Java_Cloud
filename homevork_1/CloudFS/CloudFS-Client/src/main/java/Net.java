import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Net implements Closeable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private Net(int port) throws IOException {
        socket = new Socket("localhost", port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public static Net start(int port) throws IOException {
        return new Net(port);
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    public String getMessage() throws IOException {
        return in.readUTF();
    }

    public byte[] getData() throws IOException {
        byte[] data = new byte[1024];
        in.read(data);
        return data;
    }

    public void sendData(byte[] data) throws IOException {
        out.write(data);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
