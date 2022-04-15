package com.sentinel.provider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {
    /**
     * 测试异常降级
     * @return
     */
    @RequestMapping("/providerService")
    public String provideService(){
        return "provider正常提供了服务";
    }
}
