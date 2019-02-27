package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.entity.Sva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SvaController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;

    @RequestMapping("/subHperf")
    public String subHperf(){
        subscriptionService.subscribeHeavyLoad(sva);
        return  "success";
    }

    @RequestMapping("/hperfdef")
    public String subHperf(int type){
        subscriptionService.subscribeHeavyLoad(sva);
        return  "success";
    }
}
