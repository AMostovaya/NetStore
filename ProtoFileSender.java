package cloud_netstore.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProtoFileSender {

    public static void sendFile(Path path, Channel channel, ChannelFutureListener finishListener) throws IOException {
        FileRegion region = new DefaultFileRegion(new FileInputStream(path.toFile()).getChannel(), 0, Files.size(path));

        // отправка сигнального байта
        ByteBuf buf = null;
        buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte((byte) 25);
        channel.writeAndFlush(buf);
        System.out.println("Сигнальный байт отправлен!");

        // длина имени файла
        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        int fileLength = path.getFileName().toString().length();
        buf.writeInt(fileLength);
        channel.writeAndFlush(buf);
        System.out.println("Длина имени файла: " + fileLength);

        // длина имени файла  в байтах
        byte[] filenameBytes = path.getFileName().toString().getBytes();
        buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
        buf.writeBytes(filenameBytes);
        channel.writeAndFlush(buf);
        System.out.println("Длина имени в байтах: " + filenameBytes);

        // файл
        buf = ByteBufAllocator.DEFAULT.directBuffer(8);
        Long fileSize = Files.size(path);
        buf.writeLong(fileSize);
        channel.writeAndFlush(buf);
        System.out.println("Файл отправлен! Размер файла: " + fileSize);

        ChannelFuture transferOperationFuture = channel.writeAndFlush(region);
        if (finishListener != null) {
            transferOperationFuture.addListener(finishListener);
        }
    }
}
