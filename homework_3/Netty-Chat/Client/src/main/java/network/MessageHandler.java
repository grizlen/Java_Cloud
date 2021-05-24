package network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.Message;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    private CallBack callBack;

    public MessageHandler(CallBack callBack){
        this.callBack = callBack;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        callBack.processMessage(message);
    }
}
