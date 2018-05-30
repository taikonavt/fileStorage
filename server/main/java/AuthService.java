public class AuthService {
    private static AuthService instance;
    private static boolean authorized = false;

    public static AuthService getInstance(){
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
        authorized = true;
        return true;
    }

    public static void quit(){
        authorized = false;
    }
}
