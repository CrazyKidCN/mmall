package com.crazykid.mmall;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfiguration {
    //配置Druid的监控
    //配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin"); //后台用户名
        initParams.put("loginPassword", "123456"); //后台密码
        //initParams.put("allow", "ip白名单 逗号分开");
        //initParams.put("deny", "ip黑名单 逗号分开");
        initParams.put("resetEnable", "true"); //是否启用HTML页面上的“Reset All”功能

        servletRegistrationBean.setInitParameters(initParams);
        return servletRegistrationBean;
    }

    //配置一个web监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());

        Map<String, String> initParams = new HashMap<>();
        //不过滤的话这些静态资源请求都纳入监控范围了。。。。
        initParams.put("exclusions", "*.js,*.css,/druid/*");

        filterRegistrationBean.setInitParameters(initParams);
        return filterRegistrationBean;
    }

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary //有同样的 DataSource 时，优先使用这个
    public DataSource druid() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(1); //初始化连接大小
        dataSource.setMinIdle(1); //最小连接池数量
        dataSource.setMaxActive(20); //最大连接池数量
        dataSource.setMaxWait(60000); //获取连接时最大等待时间(毫秒)
        dataSource.setTimeBetweenEvictionRunsMillis(60000); //间隔多久进行一次检测，检测需要关闭的空闲连接(毫秒)
        dataSource.setMinEvictableIdleTimeMillis(300000); //配置一个连接在池中最小生存的时间(毫秒)
        dataSource.setValidationQuery("select 1;"); //测试连接
        dataSource.setTestWhileIdle(true); //申请连接的时候检测，建议配置为true，不影响性能，并且保证安全性
        dataSource.setTestOnBorrow(false); //获取连接时执行检测，建议关闭，影响性能
        dataSource.setTestOnReturn(false); //归还连接时执行检测，建议关闭，影响性能
        dataSource.setPoolPreparedStatements(false); //是否开启PSCache，PSCache对支持游标的数据库性能提升巨大，oracle建议开启，mysql下建议关闭
        dataSource.setMaxOpenPreparedStatements(20);
        dataSource.setAsyncInit(true);
        //dataSource.setFilters();//配置扩展插件 [stat:监控统计 log4j:日志 wall:防御sql注入] 用逗号间隔
        return dataSource;
    }
}
