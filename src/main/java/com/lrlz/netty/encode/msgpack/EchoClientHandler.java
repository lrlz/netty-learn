package com.lrlz.netty.encode.msgpack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {

    private final int sendNumber;
    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    public void channelActive(ChannelHandlerContext ctx){
        UserInfo[] infos = userInfos();

        for(UserInfo userInfo:infos){
            ctx.write(userInfo);
        }
        ctx.flush();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        System.out.println("Client receive the msgpack message : " + msg);
        //ctx.write(msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    private UserInfo[] userInfos() {
        UserInfo[] userInfos = new UserInfo[sendNumber];

        UserInfo userInfo = null;
        for(int i=0;i < sendNumber;i++){
            userInfo = new UserInfo();
            userInfo.setAge(String.valueOf(i));
            userInfo.setName("ABCDEFG ---->" + i);
            userInfos[i] = userInfo;
        }
        return userInfos;
    }
}
