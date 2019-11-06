package com.levy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class InboundHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandler1.channelRead: ctx :" + ctx);
        // 通知执行下一个InboundHandler
        ByteBuf in = (ByteBuf) msg;

        System.out.println(in.toString(CharsetUtil.UTF_8));
        Thread.sleep(1000*30);

        String response = "change to xxxx";
        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
        encoded.writeBytes(response.getBytes());
        ctx.fireChannelRead(encoded);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("InboundHandler1.channelReadComplete");
        ctx.flush();
    }
}