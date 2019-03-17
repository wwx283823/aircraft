package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "HighHeavyLoadHistory")
public class HighHeavyLoadHistory implements Serializable{
    @Id
    private String  _id;
    private long timeStamp;
    private long ulSvcCellId;
    private long ulDstCellId;
    private long usKickUserCnt;
    private long adjustType;
    private long rspwrDelta;
    private long rsrpDelta;
    private String type;
}
