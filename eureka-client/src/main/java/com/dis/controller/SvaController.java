package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.entity.Sva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SvaController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;

    @RequestMapping("/subHperf")
    public String subHperf(){
        log.info("subHperf start!");
        subscriptionService.subscribeHeavyLoad(sva);
        log.info("subHperf end!");
        return  "success";
    }
}
