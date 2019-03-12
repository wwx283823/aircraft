package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "WirelessInfo")
@Data
public class WirelessInfo implements Serializable {
    @Id
    private String  _id;
    private long ULCellInterference;
    private long ucDLAvgMcs;
    private long ucDLRbRate;
    private long ucULAvgMcs;
    private long ucULRbRate;
    private long ulActiveUserNum;
    private long ulULActiveUserAvgRate;
    private long ulULCellTraffic;
    private long usAvgUserNum;
    private long usCpuRate;
    private long usMaxUserNum;
}
