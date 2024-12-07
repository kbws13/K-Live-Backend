package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoCommentMapper;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.service.VideoCommentService;

/**
* @author fangyuan
* @description 针对表【videoComment(评论表)】的数据库操作Service实现
* @createDate 2024-12-07 12:14:12
*/
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment>
    implements VideoCommentService {

}




