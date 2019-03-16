package com.dis.controller;

import com.dis.Service.SubscriptionService;
import com.dis.common.MongodbUtils;
import com.dis.entity.HeavyLoad;
import com.dis.entity.HeavyLoadParam;
import com.dis.entity.Sva;
import com.dis.entity.WirelessInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class BigTalkController {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;


    @RequestMapping("/subBigTalkByCellId")
    public String setBigTalk(HeavyLoadParam heavyLoadParam){
        String result = subscriptionService.hperfdef(sva,heavyLoadParam);
        if("success".equals(result)){
            MongodbUtils.save(heavyLoadParam,"HeavyLoadParamHistory");
        }
        return result;
    }

    @RequestMapping("/getHistoryBigTalkByCellId")
    public List<HeavyLoadParam> getHistoryBigTalkByCellId(HeavyLoadParam heavyLoad){
        HeavyLoadParam heavyLoadParam = null;
        if(heavyLoadParam==null){
            heavyLoadParam = new HeavyLoadParam();
        }
        String[] keys = {"cellId"};
        String[]  values = {heavyLoad.getCellId()};
        List<HeavyLoadParam> list = (List<HeavyLoadParam>) MongodbUtils.find(heavyLoadParam,keys,values,"HeavyLoadParamHistory");
        return list;
    }

    @RequestMapping("/getEchartsDataByCellId")
    public List<WirelessInfo> getEchartsDataByCellId(WirelessInfo wirelessInfos){
        WirelessInfo wirelessInfo = null;
        if(wirelessInfo==null){
            wirelessInfo = new WirelessInfo();
        }

        Date timestamp = new Date(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-60*1000);
        String[] keys = {"ulServiceCellId","timestamp"};
        Object[]  values = {wirelessInfos.getUlServiceCellId(),timestamp};
        List<WirelessInfo> list = (List<WirelessInfo>)MongodbUtils.findByCellIdAndGt(wirelessInfo,keys,values,"timestamp");
        return list;
    }

    @RequestMapping("/refreshEchartsDataByCellId")
    public WirelessInfo refreshEchartsDataByCellId(WirelessInfo wirelessInfo){
        String ulServiceCellId = wirelessInfo.getUlServiceCellId();
        Date timestamp = wirelessInfo.getTimestamp();
        String[] keys = {"ulServiceCellId","timestamp"};
        Object[]  values = {ulServiceCellId,timestamp};
        WirelessInfo wirelessInfo1 = (WirelessInfo)MongodbUtils.findOne(wirelessInfo,keys,values);
        return wirelessInfo1;
    }
}
