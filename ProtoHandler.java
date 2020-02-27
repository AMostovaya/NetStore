package cloud_netstore.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ProtoHandler extends ChannelInboundHandlerAdapter {

    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE, AUTH, NONE
    }

    private State currentState = State.IDLE;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;

    private String userName;
    private String userPass;

    private Path path;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        int readBytesForCommand = buf.readableBytes();
        path = Paths.get("./server_storage/");
        if (currentState == State.IDLE) {
            byte readed = buf.readByte();
            if (readed == (byte) 15) {
                currentState = State.NAME_LENGTH;
                receivedFileLength = 0L;
                System.out.println("STATE: Start file receiving");
            } else {
                System.out.println("ERROR: Invalid first byte - " + readed);
            }
        }

        if (currentState == State.NAME_LENGTH) {
            if (buf.readableBytes() >= 4) {
                System.out.println("STATE: Get filename length");
                nextLength = buf.readInt();
                currentState = State.NAME;
            }
        }

        if (currentState == State.NAME) {
            if (buf.readableBytes() >= nextLength) {
                byte[] fileName = new byte[nextLength];
                buf.readBytes(fileName);
                System.out.println("STATE: Filename received - " + new String(fileName));
                out = new BufferedOutputStream(new FileOutputStream(path.toString() + "/" + new String(fileName)));
                currentState = State.FILE_LENGTH;
            }
        }

        if (currentState == State.FILE_LENGTH) {
            if (buf.readableBytes() >= 8) {
                fileLength = buf.readLong();
                System.out.println("STATE: File length received - " + fileLength);
                currentState = State.FILE;
            }
        }

        if (currentState == State.FILE) {
            while (buf.readableBytes() > 0) {
                out.write(buf.readByte());
                receivedFileLength++;
                if (fileLength == receivedFileLength) {
                    currentState = State.IDLE;
                    System.out.println("File received");
                    out.close();
                    break;
                }
            }
        }

        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}
