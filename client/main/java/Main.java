import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Main extends Application implements CommonConst, Server_API{
    Socket socket = null;
    ObjectEncoderOutputStream oeos = null;
    ObjectDecoderInputStream odis;

    private String login;
    private boolean isConnected = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // открываю соединение с сервером, посылаю echo сообщение, сервер присылает ответ
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(SERVER_URL, PORT);
                oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
                odis = new ObjectDecoderInputStream(socket.getInputStream());

                sendMessage(ECHO);
                Message reply = (Message) odis.readObject();
                if (reply.getCmd().startsWith(ECHO)){
                    isConnected = true;
                    System.out.println("Connection - OK");
                }
                if (reply.getCmd().startsWith(FILE_OK)){

                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // запускаю графическую оболочку, отправляю в controller экземпляр main
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setMain(this);

        primaryStage.setTitle("GeekCloud Client");
        primaryStage.setScene(new Scene(root, 800.0D, 600.0D));
        primaryStage.show();

        // в новом потоке слушаю входящие сообщения
        new Thread(()->{
            try {
                odis = new ObjectDecoderInputStream(socket.getInputStream());
                Message reply;
                while (isConnected){
                    reply = (Message) odis.readObject();
                    System.out.println(reply.getCmd());
                    if (reply.getCmd().startsWith(AUTH_SUCCESSFUl)){
                        controller.setAuthOK();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        isConnected = false;
        oeos.close();
        odis.close();
        socket.close();
    }

    // срабатывает при нажатии кнопки "Авторизоваться"
    public void auth(String login, String pass){
        this.login = login;
        Message request = new Message();
        request.setLogin(login);
        request.setPass(pass);
        request.setCmd(AUTH);
        try {
            oeos.writeObject(request);
            oeos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(Path path){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else
                        throw exc;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String cmd) throws IOException {
        Message request = new Message();
        request.setCmd(cmd);
        oeos.writeObject(request);
        oeos.flush();
    }

    public void sendItem(Path startPath, Path pathFromStart){
        try {
            File file = new File(startPath.toString() + "/" + pathFromStart.toString());
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(MBYTE);
            int partsAmount = (int) Math.ceil((float) channel.size()/MBYTE);
            int partNum = 1;
            while (channel.read(buf) != -1){
                sendData(buf, pathFromStart, partsAmount, partNum);
                partNum++;
                buf.clear();
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean sendData(ByteBuffer buf, Path pathFromStart, int partsAmount, int partNum)
            throws IOException {
        Data data = new Data();
        data.setLogin(login);
        data.writeData(buf);
        data.setPath(pathFromStart);
        data.setPartsAmount(partsAmount);
        data.setPartNum(partNum);
        oeos.writeObject(data);
        oeos.flush();

        //TODO make sending parts of file
        return true;
    }
}
