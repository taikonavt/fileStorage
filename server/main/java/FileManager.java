import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager implements ServerConst{
    private static FileManager instance;

    private FileManager(){
    }

    public static FileManager getInstance(){
        if (instance == null){
            instance = new FileManager();
        }
        return instance;
    }

    public List getList(String login, Path path){
        Path authPath = Paths.get(startPath.toString(), login);
        List list = CommonMethods.getList(authPath, path);
        return list;
    }

    public void receiveFile(Data data){
        Path path = Paths.get(startPath.toString(), data.getLogin());
        CommonMethods.toAssembleFile(path, data);
    }
//
//    public Data[] getItem(Path path){
//        return CommonMethods.getData(null, path);
//    }

    public void deleteItem(String login, Path path){
        Path fullPath = Paths.get(startPath.toString(), login, path.toString());
        CommonMethods.deleteItem(fullPath);
    }


}
