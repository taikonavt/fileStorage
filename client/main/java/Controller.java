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
    @FXML ListView localListView;
    private ObservableList localList;
    @FXML HBox actionPanel1;
    @FXML ListView cloudListView;
    private ObservableList cloudList;
    @FXML HBox actionPanel2;

    public void onAuthBtnClick(){
        String login = loginField.getText();
        String pass = passField.getText();

        main.auth(login, pass);
    }

    public void onLocalSendBtnClick(){
        Path path = getPathToLocalSelectedItem();
        main.sendItem(path, operationProgress);
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
        main.downloadCloudItem(getPathToCloudSelectedItem());
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

        setLocalListView();
        setCloudListView();
        main.getCloudList(currentCloudDir);
    }

    public void setAuthClose(){
        authPanel.setVisible(true);

        actionPanel1.setVisible(false);
        actionPanel1.setManaged(false);
        actionPanel2.setVisible(false);
        actionPanel2.setManaged(false);
    }

    private void setLocalListView(){
        localList = FXCollections.observableArrayList();
        updateLocalList(main.getStartDir());
        localListView.setItems(localList);
    }

    void updateLocalList(Path currentDir) {
        this.currentLocaleDir = currentDir;
        localList.clear();
        localList.addAll(CommonMethods.getList(main.getStartDir(), currentLocaleDir));
    }

    void setCloudListView(){
        cloudList = FXCollections.observableArrayList();
        cloudListView.setItems(cloudList);
    }

    void updateCloudList(List list){
        cloudList.clear();
        cloudList.addAll(list);
    }

    private Path getPathToLocalSelectedItem(){
        String selectedItem = localListView.getSelectionModel().getSelectedItems().toString();
        selectedItem = selectedItem.substring(1, selectedItem.length() - 1);
        String currentDirStr = currentLocaleDir.toAbsolutePath().toString();
        return Paths.get(currentDirStr, selectedItem).normalize();
    }

    private Path getPathToCloudSelectedItem(){
        String selectedItem = cloudListView.getSelectionModel().getSelectedItems().toString();
        selectedItem = selectedItem.substring(1, selectedItem.length() - 1);
        return Paths.get(currentCloudDir.toString(), selectedItem).normalize();
    }

    public Path getCurrentLocaleDir() {
        return currentLocaleDir;
    }
}
