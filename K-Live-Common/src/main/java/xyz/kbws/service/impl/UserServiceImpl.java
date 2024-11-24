package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.model.entity.User;
import xyz.kbws.service.UserService;

/**
* @author fangyuan
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-11-24 22:14:25
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




