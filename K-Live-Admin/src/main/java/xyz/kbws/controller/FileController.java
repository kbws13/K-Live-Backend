package xyz.kbws.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.utils.FFmpegUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Resource
    private FFmpegUtil fFmpegUtil;

    @PostMapping("/uploadImage")
    public BaseResponse<String> uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) throws IOException {
        String date = DateUtil.format(DateUtil.date(), "yyyyMM");
        String folder = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_COVER + date;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.indexOf("."));
        String realFileName = RandomUtil.randomString(CommonConstant.LENGTH_30) + fileSuffix;
        String filePath = folder + "/" + realFileName;
        file.transferTo(new File(filePath));
        if (createThumbnail) {
            // 生成缩略图
            fFmpegUtil.createImageThumbnail(filePath);
        }
        String res = FileConstant.FILE_COVER + date + "/" + realFileName;
        return ResultUtils.success(res);
    }

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
