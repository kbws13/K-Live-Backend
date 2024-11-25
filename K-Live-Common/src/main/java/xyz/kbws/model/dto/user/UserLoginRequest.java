package xyz.kbws.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/25
 * @description: 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {

    private String email;

    private String password;

    private static final long serialVersionUID = 4146569412360093197L;
}
