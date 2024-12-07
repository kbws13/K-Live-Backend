package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Danmu;

/**
* @author fangyuan
* @description 针对表【danmu(弹幕表)】的数据库操作Service
* @createDate 2024-12-07 12:13:14
*/
public interface DanmuService extends IService<Danmu> {

    void saveDanmu(Danmu danmu);
}
