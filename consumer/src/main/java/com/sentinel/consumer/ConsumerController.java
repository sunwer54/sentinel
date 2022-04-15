package com.sentinel.consumer;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    /*
      Spring提供了一个RestTemplate 相当于HttpClient的请求对象,可以用来从java中发送Http请求
      发送get请求 getForEntity();
      发送post请求 postForEntity();
     */
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    /**
     * fallback 是服务回退机制(里面要写回退处理的方法名称)
     * 当前rpcProvider方法中,如果出现异常,它将中断执行,
     * 然后去执行fallback里面的指定的回退方法exceptionFallBack()
     * 注意:回退方法必须与发生异常的方法在同一个类中
     * @param data
     * @return
     */
    @RequestMapping("/rpcProvider")
    @SentinelResource(value = "rpcProvider",fallback = "exceptionFallBack")
    public String rpcProvider(String data){
        if ("苹果".equals(data)){
            throw new RuntimeException("数据是苹果异常");
        }
        /*
          向http://localhost:8001/providerService发送远程调用请求
          使用restTemplate发送的http请求,返回的对象ResponseEntity<String>中封装了
          响应码,响应头,状态(contentType,contentLength,responseBody,status(200/400/500)等等)
         */
        String rpcUrl = "http://localhost:8001/providerService";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(rpcUrl, String.class);
        //HttpStatus是一个枚举类,各种响应码:200,400,500等等都会对应了一个见名知意的字符:200-->HttpStatus.OK
        if (forEntity.getStatusCode() == HttpStatus.OK){
            return "ProviderService调用成功,结果为："+forEntity.getBody();
        }
        return "ProviderService调用失败";
    }

    /**
     * 回退方法(提供服务降级后的托底数据);
     * @param data 参数名称要与rpcProvider方法中的参数名一致
     * @param e 接收rpc方法中抛出的异常
     * @return
     * 当前这个回退方法被调用的时机: 在触发了某种限流/降级规则后都会走这个fallback里的方法
     */
    public String exceptionFallBack(String data,Throwable e){
        return "请求"+data+"数据出现了"+e;
    }
}
