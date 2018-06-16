import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// работа с данными
class FileManager implements ServerConst{
    private static FileManager instance;

    private FileManager(){
    }

    static FileManager getInstance(){
        if (instance == null){
            instance = new FileManager();
        }
        return instance;
    }

    List getList(String login, Path path){
        Path authPath = Paths.get(startPath.toString(), login);
        List list = CommonMethods.getList(authPath, path);
        return list;
    }

    void receiveFile(Data data){
        Path path = Paths.get(startPath.toString(), data.getLogin());
        CommonMethods.toAssembleFile(path, data);
    }

    Data[] getItem(String login, Path path){
        Path fullPath = Paths.get(startPath.toString(), login, path.toString());
        return CommonMethods.getData(fullPath);
    }

    void deleteItem(String login, Path path){
        Path fullPath = Paths.get(startPath.toString(), login, path.toString());
        CommonMethods.deleteItem(fullPath);
    }
}
