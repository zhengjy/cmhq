package com.cmhq.app.model.param;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
public class HomeOrderQuery {
    private String company_name ;
    private String current ;
    private List<String> range;

    private Integer page=1;
    private Integer limit=5;

    private String sd;
    private String ed;


    public String getSd() {
        if (CollectionUtils.isNotEmpty(range)){
            sd = range.get(0) + "00:00:00";
        }

        return sd;
    }

    public String getEd() {
        if (CollectionUtils.isNotEmpty(range) && range.size() > 1){
            ed = range.get(1) + "23:59:59";
        }
        return ed;
    }
}
