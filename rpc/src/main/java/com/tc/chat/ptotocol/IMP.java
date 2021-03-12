package com.tc.chat.ptotocol;

/**
 * @author woshi
 * @date 2021/3/4
 */
public enum IMP {
    /**
     * 系统消息
     */
    SYSTEM("SYSTEM"),
    /**
     * 登录命令
     */
    LOGIN("LOGIN"),

    /**
     * 登出命令
     */
    LOGOUT("LOGOUT"),
    /**
     * 聊天命令
     */
    CHAT("CHAT"),
    /***
     * 送花
     */
    FLOWER("FLOWER");

    private String name;

    public static boolean isIMP(String content){
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
    }

    IMP(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}