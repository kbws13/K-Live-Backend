package xyz.kbws.redis;

import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Component;
import xyz.kbws.constant.RedisConstant;
import xyz.kbws.model.entity.Category;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.utils.JwtUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Component
public class RedisComponent {

    @Resource
    private RedisUtils<Object> redisUtils;

    public String saveCheckCode(String code) {
        String checkCodeKey = RandomUtil.randomString(8);
        redisUtils.setEx(RedisConstant.CHECK_CODE + checkCodeKey, code, RedisConstant.TIME_1MIN);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(RedisConstant.CHECK_CODE + checkCodeKey);
    }

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(RedisConstant.CHECK_CODE + checkCodeKey);
    }

    public void saveUserVO(UserVO userVO) {
        String token = JwtUtil.createToken(userVO.getId(), userVO.getUserRole());
        long expireTime = System.currentTimeMillis() + RedisConstant.TIME_1DAY * 7L;
        userVO.setExpireAt(expireTime);
        userVO.setToken(token);
        redisUtils.setEx(RedisConstant.TOKEN_WEB + userVO.getId(), userVO, expireTime);
    }

    public UserVO getUserVO(String token) {
        String userId = JwtUtil.getUserId(token);
        return (UserVO) redisUtils.get(RedisConstant.TOKEN_WEB + userId);
    }

    public void cleanToken(String token) {
        String userId = JwtUtil.getUserId(token);
        redisUtils.delete(RedisConstant.TOKEN_WEB + userId);
    }

    public void saveCategoryList(List<Category> categoryList) {
        redisUtils.set(RedisConstant.CATEGORY_LIST, categoryList);
    }
}
