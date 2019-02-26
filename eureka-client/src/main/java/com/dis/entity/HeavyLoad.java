package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "heavyLoad")
@Data
public class HeavyLoad  implements Serializable {

    private Object _id;

    private Date timestamp;

    private Long svcCellId;
    //用户数
    private int userCnt;

    //干扰率
    private String ULCellInterference;
    //上行rpr利用率
    private String ucULRbRate;
    //下行rpr利用率
    private String ucDLRbRate;
}
