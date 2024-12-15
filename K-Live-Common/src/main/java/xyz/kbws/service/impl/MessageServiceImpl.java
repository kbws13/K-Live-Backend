package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.MessageMapper;
import xyz.kbws.model.entity.Message;
import xyz.kbws.service.MessageService;

/**
* @author fangyuan
* @description 针对表【message(用户消息表)】的数据库操作Service实现
* @createDate 2024-12-15 12:08:31
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService {

}




