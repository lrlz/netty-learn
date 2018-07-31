package com.lrlz.netty.nio.timeserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient {

    public static void main(String []args) {
        int port = 8080;

        if (args != null && args.length > 0) {

            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            }
        }

        new Thread(new TimeClientHandle("127.0.0.1", port), "NIO-TIMECLIENT-001").start();

    }
}
