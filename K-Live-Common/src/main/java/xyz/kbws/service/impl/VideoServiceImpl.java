package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.entity.Video;
import xyz.kbws.service.VideoService;

/**
* @author fangyuan
* @description 针对表【video(视频信息表)】的数据库操作Service实现
* @createDate 2024-11-28 20:36:09
*/
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService {

}




