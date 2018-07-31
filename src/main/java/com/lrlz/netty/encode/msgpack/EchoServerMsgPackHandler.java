package com.lrlz.netty.encode.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerMsgPackHandler extends ChannelHandlerAdapter {

    int counter = 0;

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        try {
            //直接输出msg
            System.out.println(msg.toString());
            String remsg = new String("has receive");
            //回复has receive 给客户端
            ctx.write(msg);
            System.out.println("send reply to client");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
    }
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

}
