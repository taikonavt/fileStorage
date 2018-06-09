import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileAssembler {
    private static FileAssembler instance;
    private static Data data;

    private FileAssembler(){

    }

    public static FileAssembler getInstance() {
        if (instance == null){
            instance = new FileAssembler();
        }
        return instance;
    }

    public static void put(Data data){

    }


}
