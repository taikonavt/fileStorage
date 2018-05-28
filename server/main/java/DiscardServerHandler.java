import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
//        // Discard the received data silently.
//        ((ByteBuf) msg).release(); // (3)
//    }
//
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
         cause.printStackTrace();
         ctx.close();
    }

//    PRINT INPUT MSG TO CONSOLE
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) { // (1)
                System.out.print((int) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (2)
        }
    }

//     ECHO
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        ctx.write(msg); // (1)
//        ctx.flush(); // (2)
//    }
}
