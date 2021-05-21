package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * 持有用户信息,用于代替session对象.
 * session对象可以直接持有数据，并且是线程隔离的
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();//以线程为key存取值

    public void setUser(User user) {
        users.set(user);
        //System.out.println(users);//可以得出，一个账号进不同页面，是同一对象。不同账号进多个页面，还是同一users对象
        //System.out.println(Thread.currentThread());//每一次请求，不管是不是同一个登录用户的请求，线程t都不相同。
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

/*
    @PreDestroy
    public void destroy() throws IOException {
        //删除ThreadLocal线程
        if (users!=null){
            users.remove();
        }
    }
*/

}
