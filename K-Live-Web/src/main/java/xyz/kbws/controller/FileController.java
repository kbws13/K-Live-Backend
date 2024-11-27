package xyz.kbws.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.exception.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * @author kbws
 * @date 2024/11/27
 * @description:
 */
@Slf4j
@Api(tags = "文件接口")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private AppConfig appConfig;

    @GetMapping("/getResource")
    public void getResource(@NotNull String sourceName, HttpServletResponse response) {
        if (StrUtil.isEmpty(sourceName) || !sourceName.contains(".") || !pathIsOk(sourceName)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String suffix = sourceName.substring(sourceName.lastIndexOf("."));
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常: {}", e.getMessage());
        }
    }

    private boolean pathIsOk(String path) {
        if (StrUtil.isEmpty(path)) {
            return true;
        }
        if(path.contains("../") && path.contains("..\\")) {
            return false;
        }
        return true;
    }
}
