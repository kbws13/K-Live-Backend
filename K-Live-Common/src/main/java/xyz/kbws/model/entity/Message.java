package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户消息表
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * 消息 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 视频 id
     */
    private String videoId;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 发生人 id
     */
    private String sendUserId;

    /**
     * 0:未读 1:已读
     */
    private Integer readType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 扩展消息
     */
    private String extendJson;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}