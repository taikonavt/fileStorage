import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Data implements Serializable{
    private String login;
    private byte [] bytes;
    private String pathStr;
    private int partsAmount;
    private int partNum;

    public void writeData(ByteBuffer buffer){
        buffer.flip();
        bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
    }

    public ByteBuffer readData(){
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return buf;
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

    public Path getPath() {
        return Paths.get(pathStr);
    }

    public void setPath(Path path) {
        pathStr = path.toString();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
