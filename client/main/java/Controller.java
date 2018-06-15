import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.nio.file.*;
import java.util.List;

public class Controller {
    private Main main;
    private Path currentLocaleDir;
    private Path currentCloudDir = Paths.get("");

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

    public void onLocalSendBtnClick(){
        Path path = getPathToLocalSelectedItem();
//        Path pathFromStart = path.subpath(main.getStartDir().getNameCount(), path.getNameCount());
        main.sendItem(path);
        main.getCloudList(currentCloudDir);
    }

    public void onLocalDeleteBtnClick(){
        Path path = getPathToLocalSelectedItem();
        if (!path.equals(main.getStartDir())) {
            main.deleteItem(path);
        }
        updateLocalList(currentLocaleDir);
    }

    public void onLocalUpdateBtnClick(){
        updateLocalList(currentLocaleDir);
    }

    public void handleMouseClick(MouseEvent arg){
        if (arg.getClickCount() == 2) {
            Path path = getPathToLocalSelectedItem();
            if (Files.isDirectory(path)) {
                updateLocalList(path);
            }
        }
    }

    public void onCloudUpdateBtnClick(){
        main.getCloudList(currentCloudDir);
    }

    public void onCloudDeleteBtnClick() {
        main.deleteCloudItem(getPathToCloudSelectedItem());
        main.getCloudList(currentCloudDir);
    }

    public void onCloudDownloadBtnClick(){

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

        // устанавливаю в окне список файлов начальной папки
        updateLocalList(main.getStartDir());
        main.getCloudList(currentCloudDir);
    }

    public void setAuthClose(){
        authPanel.setVisible(true);

        actionPanel1.setVisible(false);
        actionPanel1.setManaged(false);
        actionPanel2.setVisible(false);
        actionPanel2.setManaged(false);
    }

    private void updateLocalList(Path currentDir) {
        this.currentLocaleDir = currentDir;
        List list = CommonMethods.getList(main.getStartDir(), currentDir);
        ObservableList observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        localList.setItems(observableList);
    }

    public void setCloudList(List list){
        ObservableList observableList = FXCollections.observableArrayList();
        observableList.addAll(list);
        cloudList.
        cloudList.setItems(observableList);
    }

    private Path getPathToLocalSelectedItem(){
        String selectedItem = localList.getSelectionModel().getSelectedItems().toString();
        selectedItem = selectedItem.substring(1, selectedItem.length() - 1);
        String currentDirStr = currentLocaleDir.toAbsolutePath().toString();
        return Paths.get(currentDirStr, selectedItem).normalize();
    }

    private Path getPathToCloudSelectedItem(){
        String selectedItem = cloudList.getSelectionModel().getSelectedItems().toString();
        selectedItem = selectedItem.substring(1, selectedItem.length() - 1);
        return Paths.get(currentCloudDir.toString(), selectedItem).normalize();
    }

    public Path getCurrentLocaleDir() {
        return currentLocaleDir;
    }
}
