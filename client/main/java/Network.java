import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;

public class Network implements CommonConst, Server_API{
    private static Network instance;
    private Socket socket = null;
    private ObjectEncoderOutputStream oeos = null;
    private ObjectDecoderInputStream odis;
    private String login = null;
    private boolean auth = false;
    private Main main;

    private Network(){
    }

    public static Network getInstance(){
        if (instance == null){
            instance = new Network();
        }
        return instance;
    }

    public boolean open(){
        // открываю соединение с сервером, посылаю echo сообщение, сервер присылает ответ
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket(SERVER_URL, PORT);
                oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
                odis = new ObjectDecoderInputStream(socket.getInputStream());

                send(new EchoMsg());
                Packet reply = (Packet) odis.readObject();
                if (reply.getCmd().startsWith(ECHO)){
                    System.out.println("Connection - OK");
                    return true;
                }
            } catch (IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean close(){
        try {
            auth = false;
            send(new CloseMsg());
            oeos.close();
            odis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket.isConnected();
    }

    public void listenInput(Main main){
        this.main = main;

        // в новом потоке слушаю входящие сообщения
        new Thread(()->{
            try {
                Packet reply;
                while (socket.isConnected()){
                    reply = (Packet) odis.readObject();
                    String cmd = reply.getCmd();
                    System.out.println(cmd);
                    switch (cmd){
                        case AUTH_SUCCESSFUl:
                            auth = true;
                            this.main.setAuth(true);
                            break;
                        case GET_LIST:
                            List list = ((ListMsg)reply).getList();
                            this.main.setCloudList(list);
                            break;
                        case UPLOAD:
                            this.main.getFile((Data)reply);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendAuthMessage(String login, String pass){
        this.login = login;
        AuthMsg msg = new AuthMsg();
        msg.setLogin(login);
        msg.setPass(pass);
        send(msg);
    }

    public void sendData(Data[] dataArray){
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i].setLogin(login);
        }
        new Thread(()-> {
            for (int i = 0; i < dataArray.length; i++) {
                send(dataArray[i]);
            }
        }).start();
    }

    public void sendGetListMessage(Path path){
        ListMsg msg = new ListMsg();
        msg.setLogin(login);
        msg.setPath(path);
        send(msg);
    }

    public void sendDeleteFile(Path path){
        FileOperationMsg fom = new FileOperationMsg(DELETE, path);
        fom.setLogin(login);
        send(fom);
    }

    private void send(Packet packet){
        try {
            oeos.writeObject(packet);
            oeos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuth(){
        return auth;
    }
}