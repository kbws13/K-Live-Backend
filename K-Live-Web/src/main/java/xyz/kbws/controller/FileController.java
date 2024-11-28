package xyz.kbws.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.config.AppConfig;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.file.PreUploadVideoRequest;
import xyz.kbws.model.vo.UploadingFileVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
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
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @ApiOperation(value = "获取资源接口")
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

    @ApiOperation(value = "准备上传视频接口")
    @AuthCheck
    @PostMapping("/preUploadVideo")
    public BaseResponse<String> preUploadVideo(@RequestBody PreUploadVideoRequest preUploadVideoRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        String fileName = preUploadVideoRequest.getFileName();
        Integer chunks = preUploadVideoRequest.getChunks();
        String uploadId = redisComponent.savePreVideoFile(userVO.getId(), fileName, chunks);
        return ResultUtils.success(uploadId);
    }

    @ApiOperation(value = "上传视频接口")
    @AuthCheck
    @PostMapping("/uploadVideo")
    public void uploadVideo(@NotNull MultipartFile chunkFIle, @NotNull Integer chunkIndex, @NotEmpty String uploadId, HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        UploadingFileVO fileVO = redisComponent.getUploadVideoFile(userVO.getId(), uploadId);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不存在");
        }
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        if (fileVO.getFileSize() > systemSetting.getVideoSize() * FileConstant.MB_SIZE) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件超过大小限制");
        }
        // 判断分片
        if ((chunkIndex - 1) > fileVO.getChunkIndex() || chunkIndex > fileVO.getChunks() - 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        String folder = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + fileVO.getFilePath();
        File targetFile = new File(folder + "/" + chunkIndex);
        chunkFIle.transferTo(targetFile);
        fileVO.setChunkIndex(chunkIndex);
        fileVO.setFileSize(fileVO.getFileSize() + chunkFIle.getSize());
        redisComponent.updateUploadVideoFile(userVO.getId(), fileVO);
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
        if (path.contains("../") && path.contains("..\\")) {
            return false;
        }
        return true;
    }
}
