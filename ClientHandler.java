

import java.io.*;
import java.net.Socket;

public class ClientHandler {

    // **** Рабочий код отправки файла на сокетах

    public Socket socket;
    private FileInputStream fileInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public Socket getSocket() {
        return this.socket;
    }

    public Socket Connection() {
        try {
                socket = new Socket("127.0.0.1", 7411);
            } catch (IOException e) {
                e.printStackTrace();

            }

        return socket;
    }

     public void SendFile(File file, String path, Socket socket_) throws IOException {
          try {
                dataOutputStream = new DataOutputStream(socket_.getOutputStream());
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

        public void Disconnect(Socket socket_) throws IOException {
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket_ != null) socket_.close();
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
