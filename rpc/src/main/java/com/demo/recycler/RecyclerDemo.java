package com.demo.recycler;

import io.netty.util.Recycler;

/**
 * @author woshi
 * @date 2021/3/5
 */
public class RecyclerDemo {
    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    static class  User {
        private final Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle(){
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        User user = RECYCLER.get();
        user.recycle();
        User user2 = RECYCLER.get();
        user2.recycle();

        System.out.println(user == user2);
    }
}