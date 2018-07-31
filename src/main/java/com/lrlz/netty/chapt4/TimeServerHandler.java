package com.lrlz.netty.chapt4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        String body = (String)msg;
        System.out.println("the time server receive order:"+body + ";the counter is:" + ++counter);
        String currentTime = "query time order".equalsIgnoreCase(body)? new Date().toString():"BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable throwable){
        ctx.close();
    }

}
