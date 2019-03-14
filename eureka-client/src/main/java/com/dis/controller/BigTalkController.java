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
import java.util.List;

@Slf4j
@RestController
public class BigTalkController {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Sva sva;


    @RequestMapping("/subBigTalkByCellId")
    public void setBigTalk(HeavyLoadParam heavyLoadParam){
        subscriptionService.hperfdef(sva,heavyLoadParam);
        MongodbUtils.save(heavyLoadParam,"HeavyLoadParamHistory");
    }

    @RequestMapping("/getHistoryBigTalkByCellId")
    public List<HeavyLoadParam> getHistoryBigTalkByCellId(Long cellId){
        HeavyLoadParam heavyLoadParam = null;
        if(heavyLoadParam==null){
            heavyLoadParam = new HeavyLoadParam();
        }
        String[] keys = {"cellId"};
        Long[]  values = {cellId};
        List<HeavyLoadParam> list = (List<HeavyLoadParam>) MongodbUtils.find(heavyLoadParam,keys,values,"HeavyLoadParamHistory");
        return list;
    }

    @RequestMapping("/getEchartsDataByCellId")
    public List<WirelessInfo> getEchartsDataByCellId(long cellId){
        WirelessInfo wirelessInfo = null;
        if(wirelessInfo==null){
            wirelessInfo = new WirelessInfo();
        }
        long timestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()-60*1000;
        String[] keys = {"cellId","timestamp"};
        Long[]  values = {cellId,timestamp};
        List<WirelessInfo> list = (List<WirelessInfo>)MongodbUtils.findByCellIdAndGt(wirelessInfo,keys,values,"timestamp");
        return list;
    }

    @RequestMapping("/refreshEchartsDataByCellId")
    public WirelessInfo refreshEchartsDataByCellId(WirelessInfo wirelessInfo){
        long ulServiceCellId = wirelessInfo.getUlServiceCellId();
        long timestamp = wirelessInfo.getTimestamp();
        String[] keys = {"ulServiceCellId","timestamp"};
        Long[]  values = {ulServiceCellId,timestamp};
        WirelessInfo wirelessInfo1 = (WirelessInfo)MongodbUtils.findOne(wirelessInfo,keys,values);
        return wirelessInfo1;
    }
}
