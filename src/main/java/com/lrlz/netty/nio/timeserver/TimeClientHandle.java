package com.lrlz.netty.nio.timeserver;

import javax.imageio.IIOException;
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

public class TimeClientHandle implements Runnable {

    private String host;

    private int port;

    private Selector selector;

    private SocketChannel socketChannel;

    private volatile  boolean stop;

    public TimeClientHandle(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else  {
                    System.exit(1);
                }
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

                    System.out.println("now is " + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    key.channel();
                    sc.close();
                }
                //读到字节为0
            }
        }
    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_WRITE);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel channel) throws IOException {
        byte [] req = "query time order".getBytes();
        ByteBuffer writeBuf = ByteBuffer.allocate(1024);
        writeBuf.put(writeBuf);
        writeBuf.flip();
        channel.write(writeBuf);
        if (!writeBuf.hasRemaining()) {
            System.out.println("send to server succeed");
        }
    }

}
