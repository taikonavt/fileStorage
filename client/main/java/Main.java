import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main extends Application implements ServerConst{
    Socket socket = null;
    DataOutputStream out;
    DataInputStream in;
    private boolean isAuthorized = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(SERVER_URL, PORT);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setMain(this);

        primaryStage.setTitle("GeekCloud Client");
        primaryStage.setScene(new Scene(root, 600.0D, 600.0D));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        socket.close();
    }

    public void auth(){
        System.out.println("auth");
        try {
            out.write(2);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
