import io.netty.bootstrap.ServerBootstrap;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.concurrent.ExecutorService;

public class MainServer {

    public final static int PORT = 7411;

    public static void main(String[] args) throws Exception {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        ServerMainForm serverMainForm = new ServerMainForm();
        serverMainForm.setContentPane(serverMainForm.getPanelMain());
        serverMainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverMainForm.setSize(350,400);
        serverMainForm.setVisible(true);
        serverMainForm.setTitle("Server application");
        serverMainForm.pack();


        /*ServerSocket serverSocket = null;
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


                    //  ExecutorService bossExec = new OrderedMemoryAwareThreadPoolExecutor();
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
        }*/
    }
}
