package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.Video;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/5
 * @description:
 */
@Data
public class VideoVO extends Video implements Serializable {

    private static final long serialVersionUID = 4267928873063098972L;
    private String nickName;
    private String avatar;
}
