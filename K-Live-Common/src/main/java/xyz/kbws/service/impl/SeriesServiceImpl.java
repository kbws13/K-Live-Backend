package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.SeriesMapper;
import xyz.kbws.model.entity.Series;
import xyz.kbws.service.SeriesService;

/**
* @author fangyuan
* @description 针对表【series(视频合集表)】的数据库操作Service实现
* @createDate 2024-12-09 20:54:21
*/
@Service
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series>
    implements SeriesService {

}




