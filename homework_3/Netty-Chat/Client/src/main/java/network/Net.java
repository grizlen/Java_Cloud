package network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import model.Message;

@Slf4j
public class Net implements Runnable{

    private static Net net;
    private SocketChannel clientChanel;
    private CallBack callBack;

    public static Net getNet() {
        if (net == null){ net = new Net(); }
        return net;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void run() {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            clientChanel = socketChannel;
                            clientChanel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)), new ObjectEncoder(), new MessageHandler(callBack));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8189).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public void writeMessage(Message message){
        clientChanel.writeAndFlush(message);
    }
}
