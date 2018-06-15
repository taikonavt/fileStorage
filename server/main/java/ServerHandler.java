import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter
                    implements Server_API, ServerConst, CommonConst{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        AuthService authService = AuthService.getInstance();
        FileManager fileManager = FileManager.getInstance();

        String cmd = ((Packet) msg).getCmd();
        System.out.println(cmd);
        switch (cmd) {
            case ECHO: {
                ctx.writeAndFlush(new EchoMsg());
                break;
            }
            case AUTH: {
                AuthMsg request = (AuthMsg) msg;
                authService.authenticate(request.getLogin(), request.getPass());
                if (authService.isAuthorized(request.getLogin())) {
                    ctx.writeAndFlush(new AuthMsg(AUTH_SUCCESSFUl));
                } else {
                    ctx.writeAndFlush(new AuthMsg(AUTH_DENIED));
                }
                break;
            }
            case GET_LIST: {
                ListMsg request = (ListMsg) msg;
                Path path = Paths.get(startPath.toString(), request.getLogin(), request.getPath().toString());
                List list = fileManager.getList(request.getLogin(), path);
                ListMsg listMsg = new ListMsg();
                listMsg.setList(list);
                ctx.writeAndFlush(listMsg);
                break;
            }
            case UPLOAD: {
                Data request = (Data) msg;
                if (authService.isAuthorized(request.getLogin())) {
                    fileManager.receiveFile(request);
                }
                break;
            }
            case DOWNLOAD:{
//                FileOperationMsg fom = (FileOperationMsg) msg;
//                fileManager.getItem(fom.getPath());
            }
            case DELETE: {
                FileOperationMsg fom = (FileOperationMsg) msg;
                fileManager.deleteItem(fom.getLogin(), fom.getPath());
                break;
            }
            case CLOSE_CONNECTION: {
                authService.quit(((CloseMsg) msg).getLogin());
                break;
            }
        }
    }
}
