package com.tc.chat.ptotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;
import org.springframework.util.StringUtils;

/**
 * @author woshi
 * @date 2021/3/4
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    public String encode(IMMessage msg){
        if( null == msg ){
            return "";
        }

        String prex = "["+ msg.getCmd() +"]" + "[" + msg.getTime() + "]";

        if(IMP.LOGIN.getName().equals(msg.getCmd()) || IMP.FLOWER.getName().equals(msg.getCmd()) ){
            prex += ("["+ msg.getSender() +"][" + msg.getTerminal() + "]");
        }else if(IMP.CHAT.getName().equals(msg.getCmd())){
            prex += ("[" + msg.getSender() + "][" + msg.getReceiver() + "]");
        }else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
            prex += ("["+ msg.getOnline() +"]");
        }

        if( !StringUtils.isEmpty(msg.getContent())){
            prex += ("-" + msg.getCmd());
        }

        return prex;
    }
}