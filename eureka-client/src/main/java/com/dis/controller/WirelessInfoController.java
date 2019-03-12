package com.dis.controller;

import com.dis.common.MongodbUtils;
import com.dis.entity.HeavyLoad;
import com.dis.entity.HighHeavyLoad;
import com.dis.entity.WirelessInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class WirelessInfoController {

    @RequestMapping("/getWirelessInfoByParam")
    public List<WirelessInfo> getWirelessInfoByParam(HeavyLoad heavyLoad){
        String[] keys = new String[4];
        Long[] values = new Long[4];
        if(heavyLoad.getUcDLRbRate()!=null){
            values[0] = heavyLoad.getUcDLRbRate();
            keys[0] = "ucDLRbRate";
        }
        if(heavyLoad.getCellInterference()!=null){
            values[1] = heavyLoad.getCellInterference();
            keys[1] = "ulCellInterference";
        }
        if(heavyLoad.getUcULRbRate()!=null){
            values[2] = heavyLoad.getUcULRbRate();
            keys[2] = "ucULRbRate";
        }
        if(heavyLoad.getUserCnt()!=null){
            values[3] =heavyLoad.getUserCnt();
            keys[3] = "usMaxUserNum";
        }
        List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findByGt(new WirelessInfo(),keys,values);
        return  wirelessInfoList;
    }

    @RequestMapping("/getWirelessInfos")
    public List<WirelessInfo> getWirelessInfos(){
        List<WirelessInfo> wirelessInfoList = (List<WirelessInfo>)MongodbUtils.findAll(new WirelessInfo());
        return  wirelessInfoList;
    }

}
