package xyz.kbws.utils;

import org.springframework.stereotype.Component;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.FileConstant;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/11/27
 * @description: FFmpeg 工具类
 */
@Component
public class FFmpegUtil {

    @Resource
    private AppConfig appConfig;

    public void createImageThumbnail(String filePath) {
        String cmd = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        cmd = String.format(cmd, filePath, filePath + FileConstant.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtil.executeCommand(cmd, appConfig.getShowFFmpegLog());
    }
}
