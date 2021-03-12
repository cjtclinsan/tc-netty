package com.tc.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.tc.chat.ptotocol.IMDecoder;
import com.tc.chat.ptotocol.IMEncoder;
import com.tc.chat.ptotocol.IMMessage;
import com.tc.chat.ptotocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author woshi
 * @date 2021/3/4
 */
public class MsgProcessor {
    /**记录在线用户*/
    private static ChannelGroup onlineusers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**定义一些扩展属性*/
    public static final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    public static final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    public static final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");
    public static final AttributeKey<String> FROM = AttributeKey.valueOf("from");

    /**
     * 自定义解码器
     */
    private IMDecoder decoder = new IMDecoder();

    /**
     * 自定义编码器
     */
    private IMEncoder encoder = new IMEncoder();

    /**
     * 获取用户昵称
     */
    public String getNickName(Channel client){
        return client.attr(NICK_NAME).get();
    }

    /**
     * 获取用户远程 IP 地址
     */
    public String getAddress(Channel client){
        return client.remoteAddress().toString().replaceFirst("/", "");
    }

    /**
     * 获取扩展属性
     */
    public JSONObject getAttrs(Channel client){
        try {
            return client.attr(ATTRS).get();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取扩展属性
     */
    private void setAttrs(Channel client, String key, Object value){
        try {
            JSONObject json = client.attr(ATTRS).get();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        } catch (Exception e){
            JSONObject json = new JSONObject();
            json.put(key, value);
            client.attr(ATTRS).set(json);
        }
    }

    /**
     * 退出通知
     */
    public void logout(Channel client){
        // 如果 nickName 为 null，没有遵从连天连接协议，表述为非法登录
        if (getNickName(client) == null){return;}
        for (Channel channel : onlineusers) {
            IMMessage request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineusers.size(), getNickName(client)+"离开聊天室");
            String content = encoder.encode(request);
            channel.writeAndFlush(new TextWebSocketFrame(content));
        }

        onlineusers.remove(client);
    }

    /**
     * 获取系统时间
     * @return
     */
    private long sysTime() {
        return System.currentTimeMillis();
    }

    /**
     * 发送消息
     */
    public void sendMsg(Channel client, IMMessage msg){
        sendMsg(client, encoder.encode(msg));
    }

    public void sendMsg(Channel client, String msg){
        IMMessage request = decoder.decode(msg);
        if( null == request ){
            return;
        }

        String addr = getAddress(client);

        if( request.getCmd().equals(IMP.LOGIN.getName()) ){
            // 登录
            client.attr(NICK_NAME).getAndSet(request.getSender());
            client.attr(IP_ADDR).getAndSet(addr);
            client.attr(FROM).getAndSet(request.getTerminal());
            System.out.println(client.attr(FROM).get());
            onlineusers.add(client);

            for (Channel channel : onlineusers) {
                boolean isself = (channel == client);
                if( !isself ){
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineusers.size(), getNickName(client)+"加入聊天");
                }else {
                    request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineusers.size(), "已与服务器建立连接");
                }

                if("Console".equals(channel.attr(FROM).get())){
                    channel.writeAndFlush(request);
                    continue;
                }

                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }else if(request.getCmd().equals(IMP.CHAT.getName())){
            // 聊天
            for (Channel channel : onlineusers) {
                boolean isself = (channel == client);
                if(isself){
                    request.setSender("you");
                }else {
                    request.setSender(getNickName(client));
                }
                request.setTime(sysTime());

                if("Console".equals(channel.attr(FROM).get()) && !isself){
                    channel.writeAndFlush(request);
                    continue;
                }

                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }else if(request.getCmd().equals(IMP.FLOWER.getName())){
            JSONObject attrs = getAttrs(client);
            long currTime = sysTime();
            if( null != attrs ){
                long lastTime = attrs.getLongValue("lastFlowerTime");
                // 防止刷屏送花
                int seconds = 10;
                long sub = currTime - lastTime;
                if( sub < 1000 * seconds ){
                    request.setSender("you");
                    request.setCmd(IMP.FLOWER.getName());
                    request.setContent("老板谨慎消费，请"+ (seconds - Math.round(sub / 1000)) + "秒后再试");
                    String content = encoder.encode(request);
                    client.writeAndFlush(new TextWebSocketFrame(content));
                    return;
                }
            }

            // 正常送花
            for (Channel channel : onlineusers) {
                if( channel == client ){
                    request.setSender("you");
                    request.setContent("你给大家送了一朵鲜花");
                    setAttrs(client, "lastFlowerTime", currTime);
                }else {
                    request.setSender(getNickName(client));
                    request.setContent(getNickName(client)+"给大家送了一朵鲜花");
                }

                request.setTime(sysTime());
                String content = encoder.encode(request);
                channel.writeAndFlush(new TextWebSocketFrame(content));
            }
        }
    }
}