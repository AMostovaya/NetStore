import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainClient {

    public final static int PORT = 7411;
    public final static String SERVER = "127.0.0.1";
    public final static String PATH_TO_FILE = "g:/temp/1.txt"; // файл-источник

    public static void main(String[] args) throws Exception {

        Socket socket = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;

        try {

            socket = new Socket(SERVER, PORT);
            System.out.println("Connecting...");

            // отправляем файл
            File myFile = new File (PATH_TO_FILE);
            byte [] bytes  = new byte [(int)myFile.length()];

            fileInputStream = new FileInputStream(myFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(bytes,0, bytes.length);
            outputStream = socket.getOutputStream();

            System.out.println("Sending " + PATH_TO_FILE + "(" + bytes.length + " bytes)");
            outputStream.write(bytes,0, bytes.length);
            outputStream.flush();

            System.out.println("Ready!");

        } finally {

            if (bufferedInputStream != null) bufferedInputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();

        }

    }


}
