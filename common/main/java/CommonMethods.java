import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

// общие для клиента и сервера методы, работа с файлами
public class CommonMethods implements CommonConst{

    // разбивает файл на куски, возвращает массив пакетов для отправки
    public static Data[] getData(Path path){
        Data[] dataArray = new Data[0];
        try {
            RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(PART_SIZE);
            int partsAmount = (int) Math.ceil((float) channel.size()/ PART_SIZE);
            int partNum = 0;
            dataArray = new Data[partsAmount];
            while (channel.read(buf) != -1){
                Data data = constructData(buf, path, partsAmount, partNum);
                dataArray[partNum] = data;
                partNum++;
                buf.clear();
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataArray;
    }

    // упаковывает данные в пакет
    private static Data constructData(ByteBuffer buf, Path path, int partsAmount, int partNum){
        Data data = new Data();
        data.writeData(buf);
        data.setName(path.getFileName().toString());
        data.setPartsAmount(partsAmount);
        data.setPartNum(partNum);
        return data;
    }

    public static void deleteItem(Path path){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else
                        throw exc;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // собирает файл из кусков
    public static void toAssembleFile(Path startPath, Data data){
        try {

            Path path = Paths.get(startPath.toString(), data.getName());
            // TODO если первым прийдет пакет с не 0 номером?
            if (data.getPartNum() == 0){
                if (Files.exists(path))
                    Files.delete(path);
                Files.createFile(path);
            }
            RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
            FileChannel channel = raf.getChannel();
            channel.position(data.getPartNum() * PART_SIZE);
            ByteBuffer buf = data.readData();
            channel.write(buf);
            buf.clear();
            channel.close();
            raf.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static final String PREVIOUS_DIR = "..";

    // формирует лист файлов в папке
    public static List getList(Path startDir, Path path){
        List<String> list = new ArrayList<>();
        if (!path.normalize().equals(startDir)){
            list.add(PREVIOUS_DIR);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            for (Path p : stream){
                String name = p.getFileName().toString();
                if (Files.isDirectory(p)){
                    name = name + "/";
                }
                list.add(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
