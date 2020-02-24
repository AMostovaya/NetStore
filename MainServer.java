import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public final static int PORT = 7411;
    public final static String PATH_TO_FILE = "g:/temp/2.txt"; // расположение полученного файла
    public final static int FILE_SIZE = 6523399;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = null;
        Socket socket = null;
        int bytesRead;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            // старт сервера
            serverSocket = new ServerSocket(PORT);

            while (true) {
                System.out.println("Waiting connection...");
                try {
                    // ожидаем подключение
                    socket = serverSocket.accept();
                    System.out.println("Accepted connection : " + socket);

                    // получаем файл от клиента
                    byte[] bytes = new byte[FILE_SIZE];
                    InputStream inputStream = socket.getInputStream();
                    fileOutputStream = new FileOutputStream(PATH_TO_FILE);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bytesRead = inputStream.read(bytes, 0, bytes.length);
                    current = bytesRead;

                    do {
                        bytesRead = inputStream.read(bytes, current, (bytes.length - current));
                        if (bytesRead >= 0) current += bytesRead;
                    } while (bytesRead > -1);

                    bufferedOutputStream.write(bytes, 0, current);
                    bufferedOutputStream.flush();
                    System.out.println("File " + PATH_TO_FILE + " downloaded (" + current + " bytes read)");


                } finally {
                    if (bufferedOutputStream !=null) bufferedOutputStream.close();
                    if (fileOutputStream != null) fileOutputStream.close();
                    if (socket != null) socket.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
