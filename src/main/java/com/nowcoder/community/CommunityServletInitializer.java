package com.nowcoder.community;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class CommunityServletInitializer extends SpringBootServletInitializer {

    //tomcat访问该方法作为入口来运行该项目
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityApplication.class); //这里指出该项目的main方法所在的类
    }
}
