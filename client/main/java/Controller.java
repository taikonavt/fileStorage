import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.*;

public class Controller {
    private Main main;
    private Path startDir = FileSystems.getDefault().getPath("client/localStorage/").toAbsolutePath();
    private static final String PREVIOUS_DIR = "..";
    private Path currentDir;

    @FXML ProgressBar operationProgress;
    @FXML HBox authPanel;
    @FXML TextField loginField;
    @FXML TextField passField;
    @FXML ListView localList;
    @FXML HBox actionPanel1;
    @FXML ListView cloudList;
    @FXML HBox actionPanel2;

    public void onAuthBtnClick(){
        String login = loginField.getText();
        String pass = passField.getText();

        main.auth(login, pass);
    }

    public void onSendBtnClick(){
        Path path = getPathToSelectedItem();
        if (!Files.isDirectory(path)){
            Path pathFromStart = path.subpath(startDir.getNameCount(), path.getNameCount());
            main.sendItem(startDir, pathFromStart);
        }
    }

    public void onDeleteBtnClick(){
        Path path = getPathToSelectedItem();
        if (!path.equals(startDir)) {
            main.deleteItem(path);
        }
        updateLocalList(currentDir);
    }

    public void onUpdateBtnClick(){
        updateLocalList(currentDir);
    }

    public void handleMouseClick(MouseEvent arg){
        if (arg.getClickCount() == 2) {
            Path path = getPathToSelectedItem();
            if (Files.isDirectory(path)) {
                updateLocalList(path);
                currentDir = path.normalize();
            }
        }
    }

    public void setMain(Main main){
        this.main = main;
    }

    public void setAuthOK(){
        authPanel.setVisible(false);

        actionPanel1.setVisible(true);
        actionPanel1.setManaged(true);
        actionPanel2.setVisible(true);
        actionPanel2.setManaged(true);

        setLocalList();
    }

    private void setLocalList() {
        currentDir = startDir;
        updateLocalList(currentDir);
    }

    private void updateLocalList(Path path) {
        ObservableList observableList = FXCollections.observableArrayList();
        if (!path.normalize().equals(startDir)){
            observableList.add(PREVIOUS_DIR);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            for (Path p : stream){
                String name = p.getFileName().toString();
                if (Files.isDirectory(p)){
                    name = name + "/";
                }
                observableList.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        localList.setItems(observableList);
    }

    private Path getPathToSelectedItem(){
        String selectedItem = localList.getSelectionModel().getSelectedItems().toString();
        selectedItem = selectedItem.substring(1, selectedItem.length() - 1);
        String currentDirStr = currentDir.toAbsolutePath().toString();
        return Paths.get(currentDirStr, selectedItem);
    }

}
