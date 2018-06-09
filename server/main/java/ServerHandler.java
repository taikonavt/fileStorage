import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter
                    implements Server_API, ServerConst{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof Message) {
            Message message = (Message) msg;
            String cmd = message.getCmd();

            // echo отклик при открытии соединения
            if (cmd.startsWith(ECHO)) {
                System.out.println(cmd);
                Message reply = new Message();
                reply.setCmd(ECHO);
                ctx.write(reply);
                ctx.flush();
            }
            // аутентификация
            if (cmd.startsWith(AUTH)) {
                AuthService.authenticate(message.getLogin(), message.getPass());
                Message reply = new Message();
                if (AuthService.isAuthorized(message.getLogin())) {
                    reply.setCmd(AUTH_SUCCESSFUl);
                    ctx.writeAndFlush(reply);
                } else {
                    reply.setCmd(AUTH_DENIED);
                    ctx.writeAndFlush(reply);
                }
            }
            if (AuthService.isAuthorized(message.getLogin())) {

            }
        }

        if (msg instanceof Data) {
            Data data = (Data) msg;
            if (AuthService.isAuthorized(data.getLogin())) {

//            FileAssembler.put(data);
                try {
                    Path authPath = Paths.get(startPath.toString(), data.getLogin());
                    Path pathFromStart = data.getPath();
                    Path path = Paths.get(authPath.toString(), pathFromStart.toString());
                    if (data.getPartNum() == 1) {
                        if (Files.exists(path)) {
                            Files.delete(path);
                        }
                        Files.createFile(path);
                    }
                    File file = path.toFile();
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    FileChannel channel = raf.getChannel();
                    ByteBuffer buf = data.readData();
                    channel.write(buf);
                    buf.clear();
                    channel.close();
                    raf.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
