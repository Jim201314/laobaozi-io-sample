package com.laobaozi.nio;

import com.laobaozi.common.InputUtil;
import com.laobaozi.common.ServerInfo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(ServerInfo.HOST, ServerInfo.PORT));
        ByteBuffer buf = ByteBuffer.allocate(50);
        boolean flag = true;
        while (flag) {
            buf.clear();
            String input = InputUtil.getLine("Input something:").trim();
            if (input.equalsIgnoreCase("bye")) {
                flag = false;
            }
            input += "\n";
            buf.put(input.getBytes());
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
            int read = socketChannel.read(buf);
            String readMessage = new String(buf.array(), 0, read);
            System.out.println("Read resp - " + readMessage);
        }
        socketChannel.close();


    }

}
