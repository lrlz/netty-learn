package com.lrlz.netty.encode.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        byte [] arr;
        int length = msg.readableBytes();
        arr = new byte[length];
        msg.getBytes(msg.readerIndex(), arr, 0, length);
        MessagePack msgPack = new MessagePack();
        out.add(msgPack.read(arr));
    }
}
