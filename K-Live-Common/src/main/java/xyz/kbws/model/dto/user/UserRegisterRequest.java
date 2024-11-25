package xyz.kbws.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/25
 * @description: 用户注册
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    private String checkPassword;

    private String checkCodeKey;

    private String checkCode;

    private static final long serialVersionUID = -6284538547202624163L;
}
