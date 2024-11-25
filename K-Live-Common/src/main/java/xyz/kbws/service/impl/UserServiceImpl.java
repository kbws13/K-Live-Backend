package xyz.kbws.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.model.dto.user.UserLoginRequest;
import xyz.kbws.model.dto.user.UserRegisterRequest;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.enums.UserRoleEnum;
import xyz.kbws.model.enums.UserSexEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author fangyuan
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-11-24 22:14:25
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private RedisComponent redisComponent;

    @Override
    public Boolean register(UserRegisterRequest userRegisterRequest) {
        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userRegisterRequest.getEmail())
                .or().eq("nickName", userRegisterRequest.getNickName());
        User one = this.getOne(queryWrapper);
        if (one != null) {
            return false;
        }
        User user = new User();
        user.setId(RandomUtil.randomNumbers(UserConstant.LENGTH_10));
        user.setNickName(userRegisterRequest.getNickName());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(SecureUtil.md5(userRegisterRequest.getPassword()));
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setSex(UserSexEnum.SECRECY.getValue());
        user.setTheme(UserConstant.ONE);
        // TODO 初始化用户的硬币
        user.setCurrentCoinCount(10);
        user.setTotalCoinCount(10);
        return this.save(user);
    }

    @Override
    public UserVO login(UserLoginRequest userLoginRequest, String ip) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userLoginRequest.getEmail());
        User user = this.getOne(queryWrapper);
        if (user == null || !user.getPassword().equals(userLoginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        if (user.getUserRole().equals(UserRoleEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已被禁用");
        }
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        this.updateById(user);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        // TODO 设置粉丝数、关注数、硬币数
        redisComponent.saveUserVO(userVO);
        return userVO;
    }
}




