package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 关注表
 * @TableName focus
 */
@TableName(value ="focus")
@Data
public class Focus implements Serializable {
    /**
     * 用户 id
     */
    private String userId;

    /**
     * 用户 id
     */
    private String focusUserId;

    /**
     * 关注时间
     */
    private Date focusTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}