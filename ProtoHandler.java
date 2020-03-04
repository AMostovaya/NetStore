package com.amostovaya.netstore.server;

import com.amostovaya.netstore.common.ProtoFileSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

public class ProtoHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf tmp;

    public enum State {
        NAME_LENGTH, NAME, FILE_LENGTH, FILE, AUTH, SEND_FILE, WAITING_COMMANDS, WAITING_FILE
    }

    private State currentState = State.WAITING_COMMANDS;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;
    private String fileNameString;

    private String userName;
    private String userPass;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler added");
        tmp = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler removed");
        tmp.release();
        tmp = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

       ByteBuf buf = ((ByteBuf) msg);

       while (buf.readableBytes()>0) {

           if (currentState == State.WAITING_COMMANDS) {
               byte readed = buf.readByte();
               if (readed == (byte) 14) {
                   System.out.println("Command: загрузка файла на сервер");
                   receivedFileLength = 0L;
                   currentState = State.WAITING_FILE;
               }
               if (readed == (byte) 15) {
                   currentState = State.SEND_FILE;
                   System.out.println("Command: передать файл клиенту");
               }
           }

           if (currentState == State.WAITING_FILE) {
              if (buf.readableBytes() >= 4) {
                 // получаем длину имени файла
                  nextLength = buf.readInt();
                  currentState = State.NAME;
              }

               if (currentState == State.NAME) {
                   if (buf.readableBytes() >= nextLength) {
                       byte[] fileName = new byte[nextLength];
                       buf.readBytes(fileName);
                       System.out.println("Имя файла: " + new String(fileName, "UTF-8"));
                       out = new BufferedOutputStream(new FileOutputStream("//server_storage/" + new String(fileName)));
                       currentState = State.FILE_LENGTH;
                   }
               }
               if (currentState == State.FILE_LENGTH) {
                   if (buf.readableBytes() >= 8) {
                       fileLength = buf.readLong();
                       System.out.println("Размер файла: " + fileLength);
                       currentState = State.FILE;
                   }
               }
               // получаем файл
               if (currentState == State.FILE) {
                   while (buf.readableBytes() > 0) {
                       out.write(buf.readByte());
                       receivedFileLength++;
                       if (fileLength == receivedFileLength) {
                           currentState = State.WAITING_COMMANDS;
                           System.out.println("Файл загружен");
                           out.close();
                           break;
                       }
                   }
               }
           }

           if (currentState == State.SEND_FILE) {

               if (buf.readableBytes() >= 4) {
                   System.out.println("Получено имя файла");
                   nextLength = buf.readInt();
                   currentState = State.NAME;
               }

               if (currentState == State.NAME) {
                   if (buf.readableBytes() >= nextLength) {
                       byte[] fileName = new byte[nextLength];
                       buf.readBytes(fileName);
                       fileNameString = new String(fileName, "UTF-8");
                       System.out.println("Файл отправлен клиенту " + fileNameString);
                       currentState = State.FILE;
                   }
               }

               if (currentState == State.FILE) {
                        ProtoFileSender.sendFile(Paths.get(fileNameString), ctx.channel(), future -> {
                            if (future.isSuccess()) {
                                System.out.println("Файл передан клиенту");
                            }
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                                System.out.println("Error: отправка файла клиенту");
                            }
                        });
               currentState = State.WAITING_COMMANDS;
               }
           }
       }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }
}