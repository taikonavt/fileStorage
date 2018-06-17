import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;

// используется для запуска оболочки, коммутации классов, работы с файлами
public class Main extends Application implements CommonConst, Server_API{
    private Network network;
    private Controller controller;
    private Path startDir = FileSystems.getDefault().getPath("client/localStorage/").toAbsolutePath();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // запускаю графическую оболочку, отправляю в controller экземпляр main
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setMain(this);

        primaryStage.setTitle("GeekCloud Client");
        primaryStage.setScene(new Scene(root, 800.0D, 600.0D));
        primaryStage.show();

        network = Network.getInstance();
        network.open();

        network.listenInput(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        network.close();
    }

    // срабатывает при нажатии кнопки "Авторизоваться"
    public void auth(String login, String pass){
        network.sendAuthMessage(login, pass);
    }

    public void deleteItem(Path path){
        CommonMethods.deleteItem(path);
    }

    public void sendItem(Path path, ProgressBar progressBar){
        getData(path);
    }

    public void setAuth(boolean isAuth){
        if (isAuth)
            controller.setAuthOK();
        else
            controller.setAuthClose();
    }

    public Path getStartDir(){
        return startDir;
    }

    public Path getCurrentDir(){
        return controller.getCurrentLocaleDir();
    }

    public void getCloudList(Path path){
        network.sendGetListMessage(path);
    }

    public void setCloudList(List list){
        controller.updateCloudList(list);
    }

    public void deleteCloudItem(Path path){
        network.sendDeleteFile(path);
    }

    public void downloadCloudItem(Path path){
        network.sendDownloadFile(path);
    }

    public void downloadFile(Data data){
        CommonMethods.toAssembleFile(getCurrentDir(), data);
        Platform.runLater(() -> {
            controller.updateLocalList(getCurrentDir());
        });
    }

    public Data[] getData(Path path){
        Data[] dataArray = new Data[0];
        try {
            RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(PART_SIZE);
            int partsAmount = (int) Math.ceil((float) channel.size()/ PART_SIZE);
            int partNum = 0;
            dataArray = new Data[partsAmount];
            while (channel.read(buf) != -1){
                Data data = constructData(buf, path, partsAmount, partNum);
                dataArray[partNum] = data;
                network.sendData(data);
                partNum++;
                buf.clear();
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataArray;
    }

    // упаковывает данные в пакет
    private static Data constructData(ByteBuffer buf, Path path, int partsAmount, int partNum){
        Data data = new Data();
        data.writeData(buf);
        data.setName(path.getFileName().toString());
        data.setPartsAmount(partsAmount);
        data.setPartNum(partNum);
        return data;
    }
}
