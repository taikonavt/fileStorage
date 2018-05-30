import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class Controller {
    private Main main;

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

    public void setMain(Main main){
        this.main = main;
    }
}
