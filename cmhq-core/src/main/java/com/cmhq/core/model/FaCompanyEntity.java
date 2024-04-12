package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@TableName("fa_company")
@Data
public class FaCompanyEntity {

    /**
     * <pre>
     *
     * </pre>
     */
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**
     * <pre>
     * 公司名字
     * </pre>
     */
    private String	companyName;

    /**
     * <pre>
     * 手机号
     * </pre>
     */
    private String	mobile;

    /**
     * <pre>
     * 密码
     * </pre>
     */
    private String	password;

    /**
     * <pre>
     * 分销人
     * </pre>
     */
    private Integer	fUser;

    /**
     * <pre>
     * 分销比例
     * </pre>
     */
    private Integer	distributionRatio;

    /**
     * <pre>
     * 最多取消订单次数
     * </pre>
     */
    private Integer	cancelMaxNum;

    /**
     * <pre>
     * 单笔取消订单金额上限
     * </pre>
     */
    private Double	cancelMaxMoney;

    /**
     * <pre>
     * 折扣比例
     * </pre>
     */
    private Integer	ratio;

    /**
     * <pre>
     * 余额
     * </pre>
     */
    private Double	money;
    /**
     * <pre>
     * 冻结金额
     * </pre>
     */
    private Double	estimatePrice;

    /**
     * <pre>
     * 添加时间
     * </pre>
     */
    private Date createTime;

    /**
     * <pre>
     * 父管理id
     * </pre>
     */
    private Integer	fid;

    /**
     * <pre>
     *
     * </pre>
     */
    private String	status;

    /**
     * <pre>
     *
     * </pre>
     */
    private String	avatar;

    /**
     * <pre>
     * 子账号一天最大单量
     * </pre>
     */
    private Integer	ziMaxNum;

    /**
     * <pre>
     * 子账号每日最大订单金额
     * </pre>
     */
    private Double	ziMaxMoney;

    /**
     * <pre>
     * 子账号单笔订单最大限制金额
     * </pre>
     */
    private Double	ziOneMaxMoney;

    /**
     * <pre>
     *
     * </pre>
     */
    private String	name;

    /**
     * <pre>
     * 提示语
     * </pre>
     */
    private String	ordertips;

    /**
     * <pre>
     * 正向逆向
     * </pre>
     */
    private String	hobbydata;

    /**
     * <pre>
     * 当天日期
     * </pre>
     */
    private String	currdaydate;
}