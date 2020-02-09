import java.io.*;
import java.net.Socket;

public class MainClient {

    public final static int PORT = 7411;
    public final static String SERVER = "127.0.0.1";
    public final static String PATH_TO_FILE = "g:/temp/1.txt"; // файл-источник

    public static void main(String[] args) throws Exception {

        Socket socket = null;
        FileInputStream fileInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {

            socket = new Socket(SERVER, PORT);
            System.out.println("Connecting...");

            // отправляем файл
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(15); // отправляем сигнальный байт

            String myFile = PATH_TO_FILE;

            int filePathSize = (int) myFile.length();
            dataOutputStream.writeInt(filePathSize); // записываем размер имени файла
            dataOutputStream.write(PATH_TO_FILE.getBytes()); // записываем имя файла
            long fileSize = new File(myFile).length();
            dataOutputStream.writeLong(fileSize); // записываем размер файла

            byte[] buf = new byte[256];

            try {
                fileInputStream = new FileInputStream(myFile);
                int n; // количество вычитанных байтов
                while ((n = fileInputStream.read(buf))!=-1){ // последовательное чтение файла по байтам
                    dataOutputStream.write(buf, 0, n);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

             System.out.println("Send: " + PATH_TO_FILE + " (" + fileSize + " bytes)");

        } finally {

            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null) socket.close();

        }

    }


}
