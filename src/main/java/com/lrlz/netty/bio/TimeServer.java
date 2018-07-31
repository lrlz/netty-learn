package com.lrlz.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {

    public static void main(String []args) throws IOException {
        int port = 8080;

        if (args != null && args.length >0) {

            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            }
        }

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("TimeServer created");
            Socket socket = null;
            TimeServerExcutePool excutePool = new TimeServerExcutePool(50, 10000);
            while (true) {
                socket = server.accept();
//                new Thread(new TimeServerHandler(socket)).start();
                excutePool.execute(new TimeServerHandler(socket));
            }
        }
        finally {
            if (server != null) {
                System.out.println("TimeServer closed");
                server.close();
                server = null;
            }
        }

    }

}
