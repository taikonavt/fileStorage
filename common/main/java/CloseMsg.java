import java.io.Serializable;

public class CloseMsg implements Packet, Server_API, Serializable{
    private String cmd = CLOSE_CONNECTION;
    private String login;

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
}
