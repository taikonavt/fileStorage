import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ListMsg implements Packet, Server_API, Serializable{
    private String cmd = GET_LIST;
    private String login;
    private List list;
    private String path;

    public void setList(List list){
        this.list = list;
    }

    public List getList() {
        return list;
    }

    public void setPath(Path path){
        this.path = path.toString();
    }

    public Path getPath(){
        return Paths.get(path);
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
}
