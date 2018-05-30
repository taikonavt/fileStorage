import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Main extends Application implements ServerConst, Server_API{
    Socket socket = null;
    ObjectEncoderOutputStream oeos = null;
    ObjectInputStream ois;
    Packet packet;
    Packet reply;
    private boolean isConnected = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        packet = new Packet();

        // открываю соединение с сервером, посылаю echo сообщение, сервер присылает ответ
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(SERVER_URL, PORT);
                oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
                packet.setCmd(ECHO);
                oeos.writeObject(packet);
                oeos.flush();
                isConnected = true;
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
        primaryStage.setScene(new Scene(root, 600.0D, 600.0D));
        primaryStage.show();

        // в новом потоке слушаю входящие сообщения
        new Thread(()->{
            try {
                while (isConnected){
                    // !!! поток зависает на этом месте и echo сообщение не читает
                    ois = new ObjectInputStream(socket.getInputStream());
                    System.out.println(ois.available());
                    if (ois.available() > 0) {
                        reply = (Packet) ois.readObject();
                        System.out.println(reply.getCmd());
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
        ois.close();
        socket.close();
    }

    // срабатывает при нажатии кнопки "Авторизоваться"
    public void auth(String login, String pass){
        packet.setLogin(login);
        packet.setPass(pass);
        packet.setCmd(AUTH);
        try {
            oeos.writeObject(packet);
            oeos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
