package com.cmhq.core.api.dto.response;

import lombok.Data;

@Data
public class ActualFeeInfoDto {
    /**超长超重*/
    public static final String FEETYPE_CCCC = "CCCC";
    /**包装费*/
    public static final String FEETYPE_QLHCF = "QLHCF";
    /**保价费*/
    public static final String FEETYPE_QLBJ = "QLBJ";
    private String feeType;

    private Double money;
}
