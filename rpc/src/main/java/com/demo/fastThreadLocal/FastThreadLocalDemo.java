package com.demo.fastThreadLocal;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author woshi
 * @date 2021/3/5
 */
public class FastThreadLocalDemo {
    final class FastThreadLocalTest extends FastThreadLocal<Object>{
        @Override
        protected Object initialValue(){
            return new Object();
        }
    }

    private final FastThreadLocalTest fastThreadLocalTest;

    public FastThreadLocalDemo() {
        fastThreadLocalTest = new FastThreadLocalTest();
    }

    public static void main(String[] args) {
        FastThreadLocalDemo fastThreadLocalDemo = new FastThreadLocalDemo();
        new Thread(()->{
            try {
                for (int i = 0; i < 10; i++) {
                    fastThreadLocalDemo.fastThreadLocalTest.set(new Object());
                    Thread.sleep(1000);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            try {
                Object obj = fastThreadLocalDemo.fastThreadLocalTest.get();
                for (int i = 0; i < 10; i++) {
                    System.out.println(obj == fastThreadLocalDemo.fastThreadLocalTest.get());
                    Thread.sleep(1000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}