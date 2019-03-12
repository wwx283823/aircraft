package com.dis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "HighHeavyLoad")
public class HighHeavyLoad implements Serializable{
    @Id
    private String  _id;
    private long ulServiceCellId;
    private long uleNodebId;
    private List<WirelessInfo> wirelessInfo;

}
