import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public final static int PORT = 7411;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = null;
        Socket socket = null;
        OutputStream fileOutputStream = null;

        try {
            // старт сервера
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Waiting connection...");
                try {
                    // ожидаем подключение
                    socket = serverSocket.accept();
                    System.out.println("Accepted connection : " + socket);
                    // DataInputStream читает в примттивные типы данных
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                    dataInputStream.read(); // читаем сигнальный байт
                    int fileLength = dataInputStream.readInt();

                    // получаем файл от клиента
                    byte[] filenameBytes = new byte[fileLength];
                    dataInputStream.read(filenameBytes); // читаем длину имени файла

                    String filename = new String(filenameBytes);
                    filename = filename.replace(".", "_get."); // здесь некий костыль, потому что файл клиента находится в другой папке
                    long fileSize = dataInputStream.readLong(); // читаем размер входящего файла

                    try {
                        fileOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
                        for (long i = 0; i<fileSize; i++){
                            fileOutputStream.write(dataInputStream.read()); //
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    System.out.println("File  downloaded! " + filename);

                } finally {
                    if (fileOutputStream != null) fileOutputStream.close();
                    if (socket != null) socket.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
