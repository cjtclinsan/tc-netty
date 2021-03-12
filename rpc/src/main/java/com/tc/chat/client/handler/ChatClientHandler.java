package com.tc.chat.client.handler;

import com.tc.chat.ptotocol.IMMessage;
import com.tc.chat.ptotocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author woshi
 * @date 2021/3/4
 */
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<IMMessage> {
    private ChannelHandlerContext ctx;
    private String nickName;
    public ChatClientHandler(String nickName){
        this.nickName = nickName;
    }

    /**
     * 启动客户端控制台
     */
    private void session(){
        new Thread(() -> {
            System.out.println(nickName + ",你好，请在控制台输入对话内容");
            IMMessage message = null;
            Scanner scanner = new Scanner(System.in);
            do {
                if(scanner.hasNext()){
                    String input = scanner.nextLine();
                    if("exit".equals(input)){
                        message = new IMMessage(IMP.LOGOUT.getName(), "Console", System.currentTimeMillis(), nickName);
                    }else {
                        message = new IMMessage(IMP.CHAT.getName(), System.currentTimeMillis(), nickName, input);
                    }
                }
            }while (sendMsg(message));
            scanner.close();
        }).start();
    }

    /**
     * TCP 链路建立成功后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage message = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), this.nickName);
        sendMsg(message);
        log.info("成功连接服务器，已执行登录操作");
        session();
    }

    /**
     * 发送消息
     * @param message
     * @return
     */
    private boolean sendMsg(IMMessage message) {
        ctx.channel().writeAndFlush(message);
        System.out.println("请继续输入开始对话...");
        return message.getCmd().equals(IMP.LOGOUT) ? false : true;
    }

    /**
     * 收到消息后调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        IMMessage message = msg;
        System.out.println((null == message.getSender() ? "" : (message.getSender()+":")) + removeHtmlTag(message.getContent()));
    }

    private String removeHtmlTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\s]*?<\\/script>";       // 定义 script 正则
        String regEx_style = "<style[^>]*?[\\s\\s]*?<\\/style>";           // 定义 Style 正则
        String regEx_html = "<[^>]+>";                                     // 定义 HTML 正则

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");       //过滤 Script 标签


        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_script.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");       //过滤 style 标签


        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");       //过滤 html 标签

        return htmlStr.trim();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("与服务器断开连接:"+cause.getMessage());
        ctx.close();
    }
}