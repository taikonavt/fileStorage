import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthService implements ServerConst{
    private static AuthService instance;
    private static boolean authorized = false;

    private AuthService(){

    }

    public AuthService getInstance(){
        if (instance == null){
            instance = new AuthService();
        }
        return instance;
    }

    public static boolean isAuthorized(String login) {
        return authorized;
    }

    public static boolean authenticate(String login, String pass){
        //TODO auth;
        Path authPath = Paths.get(startPath.toString(), login);
        if (Files.notExists(authPath)){
            try {
                Files.createDirectories(authPath);
            } catch (IOException e) {
                e.printStackTrace();
                quit();
                return false;
            }
        }
        authorized = true;
        return true;
    }

    public static void quit(){
        authorized = false;
    }
}
