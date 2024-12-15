package xyz.kbws.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.entity.Message;
import xyz.kbws.model.enums.MessageReadTypeEnum;
import xyz.kbws.model.vo.MessageCountVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.MessageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/15
 * @description: 消息接口
 */
@Api(tags = "消息接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "查询未读消息数量")
    @AuthCheck
    @GetMapping("/getNoReadCount")
    public BaseResponse<Long> getNoReadCount(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId())
                .eq("readType", MessageReadTypeEnum.NO_READ.getValue());
        long count = messageService.count(queryWrapper);
        return ResultUtils.success(count);
    }

    @ApiOperation(value = "分组获取未读消息数量")
    @AuthCheck
    @GetMapping("/getNoReadCountByGroup")
    public BaseResponse<List<MessageCountVO>> getNoReadCountByGroup(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<MessageCountVO> messageTypeNoReadCount = messageService.getMessageTypeNoReadCount(userVO.getId());
        return ResultUtils.success(messageTypeNoReadCount);
    }
}
