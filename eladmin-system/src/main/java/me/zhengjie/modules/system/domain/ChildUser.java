package me.zhengjie.modules.system.domain;

import lombok.Getter;
import lombok.Setter;
import me.zhengjie.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by Jiyang.Zheng on 2024/4/9 16:42.
 */
@Entity
@Getter
@Setter
@Table(name="sys_childuser")
public class ChildUser {
    @Id
    @Column(name = "user_id")
    @NotNull()
    private Long userId;

    /**
     * <pre>
     * 子账户的商户id
     * </pre>
     */
    private Integer childCompanyId;
    /**
     * <pre>
     * 子账号一天最大单量
     * </pre>
     */
    @NotNull
    private Integer	ziMaxNum=0;

    /**
     * <pre>
     * 子账号每日最大订单金额
     * </pre>
     */
    @NotNull
    private Double	ziMaxMoney=0D;

    /**
     * <pre>
     * 子账号单笔订单最大限制金额
     * </pre>
     */
    @NotNull
    private Double	ziOneMaxMoney=0D;
}
