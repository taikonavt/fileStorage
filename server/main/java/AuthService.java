
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class AuthService implements ServerConst{
    private static AuthService instance;
    private static Connection connection;
    private static Statement statement;

    private static final String ID_COLUMN = "id";
    private static final String TABLE_NAME = "users";
    private static final String LOGIN_COLUMN = "login";
    private static final String PASS_COLUMN = "password";
    private static final String AUTH_COLUMN = "isAuthorized";

    private AuthService(){
        try {
            connect();
//            createTable();
//            clearTable();
//            fillTable();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AuthService getInstance(){
        if (instance == null){
            instance = new AuthService();
        }
        return instance;
    }

    public boolean isAuthorized(String login) {
        try {
            String query = "SELECT " + AUTH_COLUMN +
                    " FROM " + TABLE_NAME +
                    " WHERE " + LOGIN_COLUMN + " = '" + login + "';";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                boolean result = resultSet.getBoolean(AUTH_COLUMN);
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean authenticate(String login, String pass){
        try {
            String query = "SELECT " + LOGIN_COLUMN + ", " + PASS_COLUMN +
                    " FROM " + TABLE_NAME +
                    " WHERE " + LOGIN_COLUMN + " = '" + login + "';";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                if (resultSet.getString(PASS_COLUMN).equals(pass)){
                    setIsAuthorized(login, true);
                    Path authPath = Paths.get(startPath.toString(), login);
                    if (Files.notExists(authPath)){
                        try {
                            Files.createDirectories(authPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                            quit(login);
                            return false;
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void quit(String login){
        try {
            setIsAuthorized(login, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:jcp12_db.db");
        statement = connection.createStatement();
    }

    private void createTable() throws SQLException{
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LOGIN_COLUMN + " STRING NOT NULL, " +
                PASS_COLUMN + " STRING NOT NULL, " +
                AUTH_COLUMN + " BOOLEAN NOT NULL);";
        statement.execute(query);
    }

    private void dropTable() throws SQLException {
        statement.execute("DROP TABLE " + TABLE_NAME + ";");
    }

    private void clearTable() throws SQLException{
        statement.execute("DELETE FROM " + TABLE_NAME + ";");
    }

    private void fillTable() throws SQLException{
        connection.setAutoCommit(false);
        String query = "INSERT INTO " + TABLE_NAME + " (" +
                LOGIN_COLUMN + ", " + PASS_COLUMN + ", " +  AUTH_COLUMN + ") VALUES(?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(query);
        for (int i = 0; i < 10; i++) {
            ps.setString(1, "user" + i);
            ps.setString(2, Integer.toString(i));
            ps.setBoolean(3, false);
            ps.executeUpdate();
        }

        connection.commit();
    }

    private boolean setIsAuthorized(String login, boolean isAuthorized) throws SQLException {
        String query = "UPDATE " + TABLE_NAME +
                " SET " + AUTH_COLUMN + " = " + (isAuthorized ? 1 : 0) +
                " WHERE " + LOGIN_COLUMN + " = '" + login + "';";
        int i = statement.executeUpdate(query);
        return (i > 0);
    }

    private void printAll(){
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                do {
                    System.out.println(rs.getInt(1) + " " +
                            rs.getString(2)  + " " +
                            rs.getString(3) + " " +
                            rs.getBoolean(4));
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO сделать выход если клиент не активен более 10 мин
}
