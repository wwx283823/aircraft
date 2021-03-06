package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "WirelessInfo")
@Data
public class WirelessInfo implements Serializable {
    @Id
    private String  _id;
    private long ulCellInterference;
    private long ucDLAvgMcs;
    private long ucDLRbRate;
    private long ucULAvgMcs;
    private long ucULRbRate;
    private double ulActiveUserNum;
    private double ulULActiveUserAvgRate;
    private double ulULCellTraffic;
    private long usAvgUserNum;
    private long usCpuRate;
    private long usMaxUserNum;
    private String ulServiceCellId;
    private long uleNodebId;
    private Date timestamp;
    private long myTimestamp;
    private String times;
}
