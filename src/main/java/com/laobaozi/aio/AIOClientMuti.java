package com.laobaozi.aio;

import com.laobaozi.common.InputUtil;
import com.laobaozi.common.ServerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AIOClientMuti {

    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress(ServerInfo.PORT), socketChannel,new ConnectFinishHandler());
        CountDownLatch running = new CountDownLatch(1);
        running.await();
    }

    static class ConnectFinishHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

        @Override
        public void completed(Void result, AsynchronousSocketChannel socket) {
            // 当连接完成
            // 让我们从准备读取数据并写入到socket
            String readStr = InputUtil.getLine("Input something:").trim();
            ByteBuffer buf = ByteBuffer.allocate(512);
            buf.put((readStr.trim() + "\n").getBytes());
            buf.flip();
            socket.write(buf, socket, new WriteFinishHandler());
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("Error");
            exc.printStackTrace();
        }
    }

    static class ReadFinishHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        private ByteBuffer buf;
        public ReadFinishHandler(ByteBuffer buf) {
            this.buf = buf;
        }
        @Override
        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            System.out.println("Read resp from remote is:");
            System.out.println(new String(buf.array(), 0, result));
            // let's get a new string from cmd line

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String readStr = null;
            try {
                readStr = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteBuffer buf = ByteBuffer.allocate(512);
            buf.put((readStr.trim() + "\n").getBytes());
            buf.flip();
            attachment.write(buf, attachment, new WriteFinishHandler());
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("Error when reading");
            exc.printStackTrace();
        }
    }

    static class WriteFinishHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {


        @Override
        public void completed(Integer result, AsynchronousSocketChannel attachment) {
            // already finish writing
            // let's waiting the resp
            ByteBuffer buf = ByteBuffer.allocate(512);
            attachment.read(buf, attachment, new ReadFinishHandler(buf));
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
            System.out.println("Error when writing");
            exc.printStackTrace();
        }
    }
}
