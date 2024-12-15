package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.VideoPlayHistoryMapper;
import xyz.kbws.model.entity.VideoPlayHistory;
import xyz.kbws.service.VideoPlayHistoryService;

/**
* @author fangyuan
* @description 针对表【videoPlayHistory(视频播放历史表)】的数据库操作Service实现
* @createDate 2024-12-15 12:09:56
*/
@Service
public class VideoPlayHistoryServiceImpl extends ServiceImpl<VideoPlayHistoryMapper, VideoPlayHistory>
    implements VideoPlayHistoryService {

}




