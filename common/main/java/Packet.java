import java.io.Serializable;

public class Packet implements Serializable{
    private static final long serialVersionUID = 5193392663743561680L;
    private String cmd;
    private String login;
    private String pass;

    public void setCmd(String cmd){
        this.cmd = cmd;
    }

    public String getCmd(){
        return cmd;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }
}
