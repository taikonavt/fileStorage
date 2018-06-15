import java.io.Serializable;

public class AuthMsg implements Packet, Server_API, Serializable{
    private String cmd = AUTH;
    private String login;
    private String pass;

    AuthMsg(){}

    AuthMsg(String cmd){
        this.cmd = cmd;
    }

    @Override
    public String getCmd() {
        return cmd;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
