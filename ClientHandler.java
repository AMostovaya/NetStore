package com.amostovaya.netstore.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import java.io.*;


public class ClientHandler extends ChannelInboundHandlerAdapter {

    private DataOutputStream dataOutputStream = null;

    private Thread readerThread;

    private enum State {NAME_LENGTH, NAME, WAITING_COMMAND, WAITING_FILE, FILE, FILE_LENGTH};
    private State currentState = State.WAITING_COMMAND;
    private long receivedFileLength;
    private long fileSize;
    private int nextLength;
    private BufferedOutputStream bufferedOutputStream;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = ((ByteBuf) msg);

        while (buf.readableBytes() > 0) {

            if (currentState == State.WAITING_COMMAND) {
                byte readed = buf.readByte();
                if (readed == (byte) 14){
                    System.out.println("Command: загрузка файла с сервера");
                    receivedFileLength = 0L;
                    currentState = State.WAITING_FILE;
                }
            }

            if (currentState == State.WAITING_FILE) {
                if (currentState == State.NAME_LENGTH) {
                    if (buf.readableBytes() >= 4) {
                       // длина имени файла
                        nextLength = buf.readInt();
                        currentState = State.NAME;
                    }
                }
                if (currentState == State.NAME) {
                    if (buf.readableBytes() >= nextLength) {
                        byte[] fileName = new byte[nextLength];
                        buf.readBytes(fileName);
                        System.out.println("Имя файла: " + new String(fileName,"UTF-8"));
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("client_storage/" + new String(fileName)));
                        currentState = State.FILE_LENGTH;
                    }
                }
                if (currentState == State.FILE_LENGTH) {
                    if (buf.readableBytes() >= 8) {
                        fileSize = buf.readLong();
                        System.out.println("Размер файла: " + fileSize);
                        currentState = State.FILE;
                    }
                }
                if (currentState == State.FILE) {
                    while (buf.readableBytes() > 0) {
                        bufferedOutputStream.write(buf.readByte());
                        receivedFileLength++;
                        if (fileSize == receivedFileLength) {
                            currentState = State.NAME_LENGTH;
                            System.out.println("Файл загружен");
                            bufferedOutputStream.close();
                            break;
                        }
                    }
                }
            }
        }
        if (buf.readableBytes() == 0){
            buf.release();
        }
    }


    // Этот блок нужно доработать!
    public boolean authentication(String login, String pass) throws IOException {

        String s = "/authent";
        dataOutputStream.write(s.getBytes());
        String dataAuth = login + ":" + pass;
        dataOutputStream.write(dataAuth.getBytes());

        return true;
    }

}
