import java.io.Serializable;
import java.nio.ByteBuffer;

public class Data implements Serializable, Packet, Server_API{
    private String cmd = UPLOAD;
    private String login;
    private byte [] bytes;
    private String name;
    private int partsAmount;
    private int partNum;

    public void writeData(ByteBuffer buffer){
        buffer.flip();
        bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
    }

    public ByteBuffer readData(){
        return ByteBuffer.wrap(bytes);
    }

    public int getPartsAmount() {
        return partsAmount;
    }

    public void setPartsAmount(int partsAmount) {
        this.partsAmount = partsAmount;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
