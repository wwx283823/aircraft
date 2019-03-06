package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.entity.HeavyLoadParam;
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
        subscriptionService.subscribeHeavyLoad(sva);
        return  "success";
    }
    @RequestMapping("/hperfdef")
    public String hperfdef(){
        HeavyLoadParam heavyLoadParam = new HeavyLoadParam();
        heavyLoadParam.setDlrbmaxrate(65);
        heavyLoadParam.setOpenClose(0);
        heavyLoadParam.setMaxcelluser(22);
        heavyLoadParam.setNeibouruserrate(15);
        heavyLoadParam.setRspwrdeltasw(1);
        heavyLoadParam.setRsrpdelta(2);
        heavyLoadParam.setRsrpdeltasw(1);
        heavyLoadParam.setSrspwrdeltasw(1);
        heavyLoadParam.setType(1);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(1);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }

    @RequestMapping("/hperfdef1")
    public String hperfdef1(){
        HeavyLoadParam heavyLoadParam = new HeavyLoadParam();
        heavyLoadParam.setDlrbmaxrate(65);
        heavyLoadParam.setOpenClose(1);
        heavyLoadParam.setMaxcelluser(22);
        heavyLoadParam.setNeibouruserrate(15);
        heavyLoadParam.setRspwrdeltasw(1);
        heavyLoadParam.setRsrpdelta(2);
        heavyLoadParam.setRsrpdeltasw(1);
        heavyLoadParam.setSrspwrdeltasw(1);
        heavyLoadParam.setType(1);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(12);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }

    @RequestMapping("/hperfdef0")
    public String hperfdef0(){
        HeavyLoadParam heavyLoadParam = new HeavyLoadParam();
        heavyLoadParam.setDlrbmaxrate(65);
        heavyLoadParam.setOpenClose(1);
        heavyLoadParam.setMaxcelluser(22);
        heavyLoadParam.setNeibouruserrate(15);
        heavyLoadParam.setRspwrdeltasw(1);
        heavyLoadParam.setRsrpdelta(2);
        heavyLoadParam.setRsrpdeltasw(1);
        heavyLoadParam.setSrspwrdeltasw(1);
        heavyLoadParam.setType(0);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(13);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }

    @RequestMapping("/hperfdef2")
    public String hperfdef2(){
        HeavyLoadParam heavyLoadParam = new HeavyLoadParam();
        heavyLoadParam.setDlrbmaxrate(65);
        heavyLoadParam.setOpenClose(1);
        heavyLoadParam.setMaxcelluser(22);
        heavyLoadParam.setNeibouruserrate(15);
        heavyLoadParam.setRspwrdeltasw(1);
        heavyLoadParam.setRsrpdelta(2);
        heavyLoadParam.setRsrpdeltasw(1);
        heavyLoadParam.setSrspwrdeltasw(1);
        heavyLoadParam.setType(2);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(10);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }
}
