import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class Server {
    private final ServerSocketChannel serverChannel;
    private final ByteBuffer buffer;
    private final Selector selector;
    private int counter = 0;


    public Server() throws IOException {
        buffer = ByteBuffer.allocate(256);
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8189));
        log.debug("sever started...");
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (serverChannel.isOpen()){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                    handleAccept(key);
                }
                if (key.isReadable()){
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        String user = "user" + counter++;
        SocketChannel channel = serverChannel.accept();
        log.debug("client {} accepted.", user);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, user);
    }

    private void handleRead(SelectionKey key) {
        try {
            if (key.isValid()){
                SocketChannel channel = (SocketChannel) key.channel();
                String user = (String) key.attachment();
                StringBuilder reader = new StringBuilder();
                int read = 0;
                while (true){
                    read = channel.read(buffer);
                    if (read == 0){
                        break;
                    }
                    if (read == -1){
                        channel.close();
                    }
                    buffer.flip();
                    while (buffer.hasRemaining()){
                        reader.append((char) buffer.get());
                    }
                    buffer.clear();
                }
                String cmd = reader.toString();
                String req = Commands.execute(cmd);
                if (req != null) {
                    channel.write(ByteBuffer.wrap(req.getBytes(StandardCharsets.UTF_8)));
                }
            }
        } catch (IOException e) {
            log.debug("user {} disconnected.", key.attachment());
        }
    }
}
