package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.common.MongodbUtils;
import com.dis.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class SvaController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;

    @RequestMapping("/getHighHeavyLoad")
    public List<HighHeavyLoad> getHighHeavyLoad(){
        List<? extends Object> list = MongodbUtils.findAll(new HighHeavyLoad());
        return  (List<HighHeavyLoad>) list;
    }

    @RequestMapping("/saveData")
    public String saveData(){
        List<WirelessInfo> list = new ArrayList<WirelessInfo>();
        for (int i = 0;i<5;i++){
//            int sss = Math.random()
            WirelessInfo wirelessInfo = new WirelessInfo();
            wirelessInfo.setUcDLAvgMcs(i+1);
            wirelessInfo.setUcDLRbRate(i+2);
            wirelessInfo.setUcULAvgMcs(i+3);
            wirelessInfo.setUcULRbRate(i+4);
            wirelessInfo.setUlActiveUserNum(i+5);
            wirelessInfo.setUlCellInterference(-i-6);
            wirelessInfo.setUlULActiveUserAvgRate(i+7);
            wirelessInfo.setUlULCellTraffic(i+8);
            wirelessInfo.setUsAvgUserNum(i+9);
            wirelessInfo.setUsCpuRate(i+10);
            wirelessInfo.setUsMaxUserNum(i+11);
            wirelessInfo.setUlServiceCellId(String.valueOf(Integer.parseInt(("13363688"+String.valueOf(i+1))) & 0xFF));
            wirelessInfo.setUleNodebId(i+13);
            wirelessInfo.setTimestamp(new Date());
            list.add(wirelessInfo);
        }
        MongodbUtils.saveList(list);
        return  "success";
    }
    @RequestMapping("/saveData2")
    public String saveData2(){
        List<? extends Object> list = MongodbUtils.findAll(new WirelessInfo());
        List<WirelessInfo> wirelessInfoList = new ArrayList<WirelessInfo>();
        for (Object o:list){
            WirelessInfo wirelessInfo = (WirelessInfo)o;
            wirelessInfoList.add(wirelessInfo);
        }
        HighHeavyLoad highHeavyLoad = new HighHeavyLoad();
        highHeavyLoad.setWirelessInfo(wirelessInfoList);
        highHeavyLoad.setUleNodebId(1);
        highHeavyLoad.setUlServiceCellId(2);
        MongodbUtils.save(highHeavyLoad);
        return  "success";
    }
    @RequestMapping("/subHperf")
    public String subHperf(){
        log.info("subHperf start");
        subscriptionService.subscribeHeavyLoad(sva);
        return  "success";
    }
    @RequestMapping("/getData")
    public String getData(){
        String result = "false";
        log.info("getData start");
        String[]  key ={"timestamp"};
        Date s = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-30*1000);
        Object[] values = {s};
        List<? extends Object> list = MongodbUtils.findByGtDescAndLimit(new WirelessInfo(),key,values,"timestamp",1);
        if(list!=null&&list.size()>0){
            result = "true";
        }
        return  result;
    }

    @RequestMapping("/getHistoryData")
    public String getHistoryData(){
        String result = "false";
        log.info("getHistoryData start");
        String[]  key ={"timeStamp"};
        long s = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-60*1000).getTime()/1000;
        Object[] values = {s};
        List<? extends Object> list = MongodbUtils.findByGtDescAndLimit(new HighHeavyLoadHistory(),key,values,"timeStamp",1);
        if(list!=null&&list.size()>0){
            result = "true";
        }
        return  result;
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
        heavyLoadParam.setRspwrdelta(1);
        heavyLoadParam.setType(1);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(1);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }
    @RequestMapping("/saveHistory")
    public String saveHistory(){
        HighHeavyLoadHistory heavyLoadParam = new HighHeavyLoadHistory();
        heavyLoadParam.setAdjustType(1);
        heavyLoadParam.setUlSvcCellId(16);
        heavyLoadParam.setUlDstCellId(22);
        heavyLoadParam.setUsKickUserCnt(15);
        heavyLoadParam.setRspwrDelta(1);
        heavyLoadParam.setRspwrDelta(2);
        heavyLoadParam.setType("用户数调整");
        heavyLoadParam.setBigType("频点间基于用户数的快速负载均衡");
        heavyLoadParam.setTimeStamp(new Date().getTime());
        MongodbUtils.save(heavyLoadParam);
        return  "success";
    }
    @RequestMapping("/saveHistory2")
    public String saveHistory2(){
        HighHeavyLoadHistory heavyLoadParam = new HighHeavyLoadHistory();
        heavyLoadParam.setAdjustType(1);
        heavyLoadParam.setUlSvcCellId(17);
        heavyLoadParam.setUlDstCellId(22);
        heavyLoadParam.setUsKickUserCnt(15);
        heavyLoadParam.setRspwrDelta(1);
        heavyLoadParam.setRspwrDelta(2);
        heavyLoadParam.setType("用户数调整");
        heavyLoadParam.setBigType("频点间基于用户数的快速负载均衡");
        heavyLoadParam.setTimeStamp(new Date().getTime());
        MongodbUtils.save(heavyLoadParam);
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
        heavyLoadParam.setRspwrdelta(1);
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
        heavyLoadParam.setRspwrdelta(1);
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
        heavyLoadParam.setRspwrdelta(1);
        heavyLoadParam.setType(2);
        heavyLoadParam.setUlcellmaxinterference(-115);
        heavyLoadParam.setUlrbmaxrate(50);
        heavyLoadParam.setUsercnt(10);
        heavyLoadParam.setUsercntsw(1);
        subscriptionService.hperfdef(sva,heavyLoadParam);
        return  "success";
    }
}
