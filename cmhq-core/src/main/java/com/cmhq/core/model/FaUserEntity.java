package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:14.
 */
@Data
@TableName("fa_user")
public class FaUserEntity {
    /**
     * <pre>
     * ID
     * </pre>
     */
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**
     * <pre>
     * 组别ID
     * </pre>
     */
    private Integer	groupId;

    /**
     * <pre>
     * 用户名
     * </pre>
     */
    private String	username;

    /**
     * <pre>
     * 昵称
     * </pre>
     */
    private String	nickname;

    /**
     * <pre>
     * 密码
     * </pre>
     */
    private String	password;

    /**
     * <pre>
     * 密码盐
     * </pre>
     */
    private String	salt;

    /**
     * <pre>
     * 邮箱
     * </pre>
     */
    private String	email;

    /**
     * <pre>
     * 手机号
     * </pre>
     */
    private String	mobile;

    /**
     * <pre>
     * 头像
     * </pre>
     */
    private String	avatar;

    /**
     * <pre>
     * 等级
     * </pre>
     */
    private Integer	level;

    /**
     * <pre>
     * 性别
     * </pre>
     */
    private Integer	gender;

    /**
     * <pre>
     * 生日
     * </pre>
     */
    private String	birthday;

    /**
     * <pre>
     * 身份证号
     * </pre>
     */
    private String	idcard;

    /**
     * <pre>
     * 可提现佣金
     * </pre>
     */
    private Double	money;

    /**
     * <pre>
     * 积分
     * </pre>
     */
    private Integer	score;

    /**
     * <pre>
     * 连续登录天数
     * </pre>
     */
    private Integer	successions;

    /**
     * <pre>
     * 最大连续登录天数
     * </pre>
     */
    private Integer	maxsuccessions;

    /**
     * <pre>
     * 上次登录时间
     * </pre>
     */
    private Long	prevtime;

    /**
     * <pre>
     * 登录时间
     * </pre>
     */
    private Long	logintime;

    /**
     * <pre>
     * 登录IP
     * </pre>
     */
    private String	loginip;

    /**
     * <pre>
     * 失败次数
     * </pre>
     */
    private Integer	loginfailure;

    /**
     * <pre>
     * 加入IP
     * </pre>
     */
    private String	joinip;

    /**
     * <pre>
     * 加入时间
     * </pre>
     */
    private Long	jointime;

    /**
     * <pre>
     * 创建时间
     * </pre>
     */
    private Long	createtime;

    /**
     * <pre>
     * 更新时间
     * </pre>
     */
    private Long	updatetime;

    /**
     * <pre>
     * Token
     * </pre>
     */
    private String	token;

    /**
     * <pre>
     * 状态
     * </pre>
     */
    private String	status;

    /**
     * <pre>
     * 验证
     * </pre>
     */
    private String	verification;

    /**
     * <pre>
     * openid
     * </pre>
     */
    private String	openid;

    /**
     * <pre>
     * unionid
     * </pre>
     */
    private String	unionid;

    /**
     * <pre>
     * 每天最大提现次数
     * </pre>
     */
    private Integer	maxWithdrawalNum;

    /**
     * <pre>
     * 每天最大提现金额
     * </pre>
     */
    private Double	maxWithdrawalMoney;

    /**
     * <pre>
     * 支付宝账号
     * </pre>
     */
    private String	zhiAccount;

    /**
     * <pre>
     * 支付宝名字
     * </pre>
     */
    private String	zhiName;

    /**
     * <pre>
     * 每天提现限额
     * </pre>
     */
    private Double	withdrawalMaxMoney;

    /**
     * <pre>
     * 每天最大提现次数
     * </pre>
     */
    private Integer	withdrawalMaxNum;

    /**
     * <pre>
     * 当日接收短信次数
     * </pre>
     */
    private Integer	smscode;

    /**
     * <pre>
     *
     * </pre>
     */
    private String	address;

    /**
     * <pre>
     * 已提现佣金
     * </pre>
     */
    private Double	tixian;

    /**
     * <pre>
     * 总获得的佣金
     * </pre>
     */
    private Double	yongjin;

    /**
     * <pre>
     * 0:未删除,1:已删除
     * </pre>
     */
    private Integer	delkid;
}
