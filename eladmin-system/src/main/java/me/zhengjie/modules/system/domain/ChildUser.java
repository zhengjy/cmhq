package me.zhengjie.modules.system.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Jiyang.Zheng on 2024/4/9 16:42.
 */
@Entity
@Getter
@Setter
@Table(name="sys_childuser")
public class ChildUser  implements Serializable {
    @Id
    @Column(name="user_id")
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
