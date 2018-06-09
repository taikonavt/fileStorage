import java.nio.file.FileSystems;
import java.nio.file.Path;

public interface ServerConst {
    Path startPath = FileSystems.getDefault().getPath("server/cloudStorage").toAbsolutePath();
}
