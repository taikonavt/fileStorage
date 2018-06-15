import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileOperationMsg implements Packet, Server_API, Serializable{
    private String cmd;
    private String path;
    private String login;

    FileOperationMsg(String cmd, Path path){
        this.cmd = cmd;
        this.path = path.toString();
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

    public Path getPath() {
        return Paths.get(path);
    }
}
