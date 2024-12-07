package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.ActionMapper;
import xyz.kbws.model.entity.Action;
import xyz.kbws.service.ActionService;

/**
* @author fangyuan
* @description 针对表【action(用户行为 点赞、评论)】的数据库操作Service实现
* @createDate 2024-12-07 12:13:10
*/
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
    implements ActionService {

}




