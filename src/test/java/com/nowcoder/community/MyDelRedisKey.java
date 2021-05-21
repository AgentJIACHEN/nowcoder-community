package com.nowcoder.community;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MyDelRedisKey {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void delRedisKey(){

        for(Object key:redisTemplate.keys("ticket*"))
        {
            redisTemplate.delete(key.toString());
            //System.out.println(key.toString());
        }
        //redisTemplate.delete("test:user");
    }
}
