package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.VideoPost;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/3
 * @description:
 */
@Data
public class VideoPostVO extends VideoPost implements Serializable {

    /**
     * 播放数量
     */
    private Integer playCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 弹幕数量
     */
    private Integer danmuCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 投币数量
     */
    private Integer coinCount;

    /**
     * 收藏数量
     */
    private Integer collectCount;

    /**
     * 是否推荐 0:未推荐 1:已推荐
     */
    private Integer recommendType;

    private static final long serialVersionUID = -4730719816009764374L;
}
