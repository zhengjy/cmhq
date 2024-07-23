package me.zhengjie.modules.system.domain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:14.
 */
@Entity
@Getter
@Setter
@Table(name="fa_user")
public class FaSaleUser {
    /**
     * <pre>
     * ID
     * </pre>
     */
    @Id
    private Integer	id;

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
     * 手机号
     * </pre>
     */
    private String	mobile;

}
