
public class Controller {
    private Main main;

    public void onAuthBtnClick(){
//        if (socket != null && socket.isConnected()){
//            System.out.println("connection opened");
//        } else
//            System.out.println("connection closed");
        main.auth();
    }

    public void setMain(Main main){
        this.main = main;
    }
}
