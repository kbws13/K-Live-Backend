package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.ActionMapper;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.service.ActionService;

import javax.annotation.Resource;

/**
 * @author fangyuan
 * @description 针对表【action(用户行为 点赞、评论)】的数据库操作Service实现
 * @createDate 2024-12-07 12:13:10
 */
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
        implements ActionService {

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAction(Action action) {
        Video video = videoMapper.selectById(action.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        action.setVideoUserId(video.getUserId());
        UserActionTypeEnum actionTypeEnum = UserActionTypeEnum.getEnumByValue(action.getActionType());
        if (actionTypeEnum == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        QueryWrapper<Action> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", action.getVideoId())
                .eq("commendId", action.getCommentId())
                .eq("actionType", action.getActionType())
                .eq("userId", action.getUserId());
        Action dbAction = this.getOne(queryWrapper);
        action.setActionTime(DateUtil.date());
        switch (actionTypeEnum) {
            case VIDEO_LIKE:
            case VIDEO_COLLECT:
                if (dbAction != null) {
                    this.removeById(dbAction.getId());
                } else {
                    this.save(action);
                }
                int changeCount = dbAction == null ? UserConstant.ONE : -UserConstant.ONE;
                videoMapper.updateCountInfo(action.getVideoId(), actionTypeEnum.getField(), changeCount);
                if (actionTypeEnum == UserActionTypeEnum.VIDEO_COLLECT) {
                    // TODO 更新 ES 的收藏数量
                }
                break;
            case VIDEO_COIN:
                if (video.getUserId().equals(action.getUserId())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能给自己投币");
                }
                if (dbAction != null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能重复投币");
                }
                // 减少自己的硬币
                int updateCount = userMapper.updateCoinCount(action.getUserId(), -action.getCount());
                if (updateCount == 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "硬币数量不足");
                }
                // 给 UP 主增加硬币
                updateCount = userMapper.updateCoinCount(action.getUserId(), action.getCount());
                if (updateCount == 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "投币失败");
                }
                this.save(action);
                videoMapper.updateCountInfo(action.getVideoId(), actionTypeEnum.getField(), action.getCount());
                break;
        }
    }
}




