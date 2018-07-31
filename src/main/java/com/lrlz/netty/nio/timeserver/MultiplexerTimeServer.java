package com.lrlz.netty.nio.timeserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println(" time server is start at port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private  void stop(){
        this.stop = true;
    }

    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }


                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (selector !=null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void  handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }

            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readbuf = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readbuf);

                if (readBytes > 0) {
                    readbuf.flip();
                    byte [] bytes = new byte[readbuf.remaining()];
                    readbuf.get(bytes);
                    String body = new String(bytes, "UTF-8");

                    System.out.println("time server receive " + body);
                    String currentTime = "querry time order".equalsIgnoreCase(body)?
                            new Date(System.currentTimeMillis()).toString() : "bad request";

                    doWrite(sc, currentTime);
                } else if (readBytes<0) {
                    key.channel();
                    sc.close();
                }
                //读到字节为0
            }
        }
    }


    private void doWrite(SocketChannel channel, String msg) throws IOException {
        if (msg != null &&msg.trim().length() > 0) {
            byte [] bytes = msg.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(buffer);
            buffer.flip();
            channel.write(buffer);
        }
    }

}
