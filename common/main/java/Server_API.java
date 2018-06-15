public interface Server_API {
    String CLOSE_CONNECTION = "/quit";
    String AUTH = "/auth";
    String AUTH_SUCCESSFUl = "/authok";
    String AUTH_DENIED = "/authdenied";
    String ECHO = "/echo";
    String UPLOAD = "/upload";
    String DOWNLOAD = "/download";
    String DELETE = "/delete";
    String GET_LIST = "/getlist";
}
