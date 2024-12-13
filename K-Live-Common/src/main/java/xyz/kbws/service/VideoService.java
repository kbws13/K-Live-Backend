package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Video;

/**
 * @author fangyuan
 * @description 针对表【video(视频信息表)】的数据库操作Service
 * @createDate 2024-11-28 20:36:09
 */
public interface VideoService extends IService<Video> {

    void deleteVideo(String videoId, String userId);

    void changeInteraction(String videoId, String userId, String interaction);
}
