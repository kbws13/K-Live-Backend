package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.dto.video.VideoQueryRequest;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.vo.VideoVO;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【video(视频信息表)】的数据库操作Mapper
 * @createDate 2024-11-28 20:36:09
 * @Entity generator.domain.Video
 */
public interface VideoMapper extends BaseMapper<Video> {

    List<VideoVO> queryList(@Param("videoQueryRequest") VideoQueryRequest videoQueryRequest);

    void updateCountInfo(@Param("videoId") String videoId, @Param("field") String field, @Param("changeCount") Integer changeCount);

}




