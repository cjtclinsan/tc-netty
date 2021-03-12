package com.tc.chat.ptotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义 IMP 的编码器
 * @author woshi
 * @date 2021/3/4
 */
public class IMDecoder extends ByteToMessageDecoder {
    /**解析 IM   请求内容的正则*/
    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            // 现获取可读字节数
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array, in.readerIndex(), length);

            // 空消息不解析
            if(StringUtils.isEmpty(content)){
                if( !IMP.isIMP(content) ){
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }

            in.getBytes(in.readableBytes(), array, 0, length);
            out.add(new MessagePack().read(array, IMMessage.class));
            in.clear();
        }catch (MessageTypeException e){
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 字符串解析成自定义即时通信协议
     */
    public IMMessage decode(String msg){
        if(StringUtils.isEmpty(msg)) {
            return null;
        }

        try {
            Matcher m = pattern.matcher(msg);
            String header = "";
            String context = "";
            if( m.matches() ){
                header = m.group(1);
                context = m.group(3);
            }

            String[] heards = header.split("\\]\\[");
            long time = 0;
            try {
                time = Long.parseLong(heards[1]);
            }catch (Exception e){

            }

            String nickName = heards[2];
            // 昵称最多十个字符长度
            nickName = nickName.length() < 10 ? nickName : nickName.substring(0, 9);

            if(msg.startsWith("["+ IMP.LOGIN.getName() +"]")){
                return new IMMessage(heards[0], heards[3], time, nickName);
            } else if(msg.startsWith("["+IMP.CHAT.getName()+"]")){
                return new IMMessage(heards[0], time, nickName, context);
            }else if(msg.startsWith("["+ IMP.FLOWER.getName() +"]")){
                return new IMMessage(heards[0], heards[3], time, nickName);
            }else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}