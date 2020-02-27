package cloud_netstore.client;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private FileInputStream fileInputStream = null;
    private DataOutputStream dataOutputStream = null;
    public  DataInputStream dataInputStream;
    private OutputStream fileOutputStream = null;
    private Thread readerThread;


    public String connection() {
        try {
            if (socket == null || socket.isClosed()) {

                socket = new Socket("127.0.0.1", 7411);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
             // запуск потока на прослушивание файлов от сервера
                readerThread = new Thread(new IncomingReader());
                readerThread.start();

                return "Connection is successful";
            }
            else {
                return "Already connected";
            }
        }
        catch (IOException e) {
            return "Fault connection: server closed";
        }
    }

    public class  IncomingReader implements Runnable {

        public void run() {

            try {

                while (true) {
                    dataInputStream.read(); // читаем сигнальный байт
                    int fileLength = dataInputStream.readInt();
                    System.out.println("Сигнальный массив: " + fileLength);

                    byte[] filenameBytes = new byte[fileLength];
                    dataInputStream.read(filenameBytes); // читаем длину имени файла

                    String filename = new String(filenameBytes);
                    long fileSize = dataInputStream.readLong(); // читаем размер входящего файла
                    System.out.println("Размер входящего файла " + fileSize);

                    try {
                        fileOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
                        for (long i = 0; i<fileSize; i++){
                            fileOutputStream.write(dataInputStream.read()); //
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        System.out.println("Получен файл " + filename);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                dataInputStream.close(); // нужно ли его здесь закрывать?
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void receiveFile() {

        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            byte[] bs = new byte[25];
            dataInputStream.read(bs); // читаем сигнальный байт
            int fileLength = dataInputStream.readInt();
            System.out.println("Сигнальный массив: " + fileLength);

            byte[] filenameBytes = new byte[fileLength];
            dataInputStream.read(filenameBytes); // читаем длину имени файла

            String filename = new String(filenameBytes);
            long fileSize = dataInputStream.readLong(); // читаем размер входящего файла
            System.out.println("Размер входящего файла " + fileSize);


                fileOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
                for (long i = 0; i < fileSize; i++) {
                    fileOutputStream.write(dataInputStream.read()); //
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                System.out.println("Получен файл " + filename);

            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    public boolean authentication(String login, String pass) throws IOException {

        String s = "/authent";
        dataOutputStream.write(s.getBytes());
        String dataAuth = login + ":" + pass;
        dataOutputStream.write(dataAuth.getBytes());

        // делать через datainput

        return true;
    }

    public void sendFile(File file, String path) throws IOException {
          try {

                dataOutputStream.write(15); // отправляем сигнальный байт

                int filePathSize = (int) path.length();
                dataOutputStream.writeInt(filePathSize); // записываем размер имени файла
                dataOutputStream.write(path.getBytes()); // записываем имя файла
                long fileSize = file.length();
                dataOutputStream.writeLong(fileSize); // записываем размер файла

                byte[] buf = new byte[256];

                try {
                    fileInputStream = new FileInputStream(file);
                    int n; // количество вычитанных байтов
                    while ((n = fileInputStream.read(buf))!=-1){ // последовательное чтение файла по байтам
                        dataOutputStream.write(buf, 0, n);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    public void disconnect() throws IOException {
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket!= null) socket.close();

        }


           /*

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

        }*/


}
