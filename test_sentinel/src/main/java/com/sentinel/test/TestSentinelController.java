package com.sentinel.test;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.sentinel.test.util.AsynRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

@RestController
public class TestSentinelController {

    /**
     * sentinel的流控测试:
     * QPS直接快速失败：每秒请求数达到设定阈值，对超过阈值了的请求执行拒绝
     * @return
     */
    @RequestMapping("/testSentinel")
    public String testSentinel(){
        return "testSentinel";
    }

    /**
     * sentinel的流控测试:QPS关联快速失败：
     * 比如，当访问/testSentinel的每秒请求数达到了设定阈值，对访问/testRelation的请求执行拒绝
     * @return
     */
    @RequestMapping("/testRelation")
    public String testRelation(){
        return "testRelation";
    }

    /**
     * sentinel的流控测试:QPS直接排队等待
     * 每秒钟最多只能阈值设置的限定次数，超过的请求排队等待，等待超过指定的超时时间毫秒则被拒绝执行。
     * @return
     */
    @Autowired
    private AsynRequestUtil asynRequestUtil;
    @RequestMapping("/startSendToTestWait")
    public String start(){
        for (int i = 0;i<5000;i++) {
            asynRequestUtil.asynRequest();
            try {
                TimeUnit.MILLISECONDS.sleep(50);//每秒发送20次请求
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "请求发送完毕";
    }

    int m = 0;
    @RequestMapping("/testWait")
    public String testWait(){
        System.out.println("处理请求"+ m++);
        return "testWait";
    }

    /**
     * sentinel的流控测试:QPS直接预热
     * sentinel 客户端的默认冷加载因子 coldFactor 为 3，
     * 即请求 QPS 从 threshold / 3 开始，经预热时长逐渐把阈值升至设定的 QPS 阈值。
     * 每秒超过的阈值请求都被拒绝执行
     * @return
     */
    @RequestMapping("/testWarmUp")
    public String testWarmUp(){
        for (int i = 0;i<5000;i++) {
            asynRequestUtil.asynRequest();
            try {
                TimeUnit.MILLISECONDS.sleep(50);//每秒发送20次请求
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("请求被执行");
        return "testWarmUp";
    }

    /**
     * sentinel降级测试RT:1s内持续进入5个请求，如果处理超时，熔断，直到达到时间窗口时间就恢复
     * @return
     */
    @RequestMapping("/testDegradeRT")
    public String testDegradeRT(){
        try {
            TimeUnit.MILLISECONDS.sleep(100);//响应时间100ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("请求被执行");
        return "testDegradeRT";
    }
    @RequestMapping("/toTestDegradeRT")
    public String RequestDegradeRT(){
        for (int i = 0;i<5000;i++) {
            asynRequestUtil.sendRequestToDegradeRT();
        }
        return "请求发送完毕";
    }

    /**
     * sentinel降级测试异常比例:1s内的所有请求中的异常比列达到设定值就熔断，直到达到时间窗口时间就恢复
     * @return
     */
    int a = 0;
    @RequestMapping("/testDegradeExceptionPer")
    public String testDegradePer(){
        a++;
        if (a%2==0){
            throw new RuntimeException("出现异常");
        }
        System.out.println("请求被执行");
        return "testDegradePer";
    }
    @RequestMapping("/toDegradeExceptionPer")
    public String RequestDegradeExceptionPer(){
        for (int i = 0;i<1000;i++) {
            asynRequestUtil.sendRequestToDegradeExceptionPer();
        }
        return "请求发送完毕";
    }

    /**
     * sentinel降级测试异常比例:当指定时间窗口内的所有请求中的异常个数达到设定值就熔断，直到达到时间窗口时间后就恢复
     * @return
     */
    @RequestMapping("/testDegradeExceptionNum")
    public String testDegradeNum(){
        a++;
        if (a%2==0){
            throw new RuntimeException("出现异常");
        }
        System.out.println("请求被执行");
        return "testDegradeNum";
    }

    /**
     * sentinel热点规则测试:
     * @SentinelResource 注解用来做限流控制标记资源点,给资源命名(注解中的值要与方法名一样)
     * 使用该注解可以定位到方法的参数列表资源，对参数列表中的参数实行限流
     * 在指定的窗口时长内，只能访问被指定的资源所设定的阈值的次数，超过阈值就报异常ParamFlowException，
     * 超过窗口时长后就恢复正常。
     * @return
     */
    @RequestMapping("/testHotSpot")
    @SentinelResource("testHotSpot")
    public String testHotSpot(String pname,Integer pnum){
        return pname+"---"+pnum;
    }
}
