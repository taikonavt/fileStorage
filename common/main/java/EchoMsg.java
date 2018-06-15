import java.io.Serializable;

public class EchoMsg implements Packet, Server_API, Serializable {
    private String cmd = ECHO;

    @Override
    public String getCmd() {
        return cmd;
    }

    @Override
    public String getLogin() {
        return null;
    }

    @Override
    public void setLogin(String login) {

    }
}
