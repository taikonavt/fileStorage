import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter
                    implements Server_API{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet)msg;
        String cmd = packet.getCmd();

        // echo отклик при открытии соединения
        if (cmd.startsWith(ECHO)){
            System.out.println(cmd);
            Packet reply = new Packet();
            reply.setCmd(ECHO);
            ctx.write(reply);
            ctx.flush();
            System.out.println("echo has been send");
        }
        // аутентификация
        if (cmd.startsWith(AUTH)){
            AuthService.authenticate(packet.getLogin(), packet.getPass());
            Packet reply = new Packet();
            if (AuthService.isAuthorized(packet.getLogin())){
                reply.setCmd(AUTH_SUCCESSFUl);
                ctx.write(reply);
                ctx.flush();
            } else {
                reply.setCmd(AUTH_DENIED);
                ctx.write(reply);
                ctx.flush();
            }
        }

        if (AuthService.isAuthorized(packet.getLogin())){

        }
    }
}
