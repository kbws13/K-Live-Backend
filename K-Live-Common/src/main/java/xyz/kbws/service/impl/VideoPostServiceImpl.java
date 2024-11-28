package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.service.VideoPostService;

/**
* @author fangyuan
* @description 针对表【videoPost(已发布视频信息表)】的数据库操作Service实现
* @createDate 2024-11-28 20:36:20
*/
@Service
public class VideoPostServiceImpl extends ServiceImpl<VideoPostMapper, VideoPost>
    implements VideoPostService {

}




