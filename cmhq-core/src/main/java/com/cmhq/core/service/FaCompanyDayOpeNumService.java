package com.cmhq.core.service;

/**
 * Created by Jiyang.Zheng on 2024/4/22 15:10.
 */
public interface FaCompanyDayOpeNumService {

    double selectValue( String startDate,String endDate, String opeType);

    void  updateValue(String date, String opeType,double value);
}
