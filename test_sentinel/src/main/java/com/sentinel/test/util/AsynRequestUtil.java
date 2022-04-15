package com.sentinel.test.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 创建异步任务的类，里面的方法是异步的（不需要等待上次调用处理的结果，可以连续调用doSome方法）
 */
@Component
public class AsynRequestUtil {
    /**
     * 模拟异步发送请求
     * @Async: 该注解标注在方法上，表示该方法是异步的
     * 被修改的方法在被调用时，在一个新的线程中执行
     */
    @Async
    public void asynRequest(){
        HttpClientUtil.doGet("http://localhost:8005/testWait");
    }

    @Async
    public void sendRequestToDegradeRT(){
        HttpClientUtil.doGet("http://localhost:8005/testDegradeRT");
    }

    @Async
    public void sendRequestToDegradeExceptionPer(){
        HttpClientUtil.doGet("http://localhost:8005/testDegradeExceptionPer");
    }
}
