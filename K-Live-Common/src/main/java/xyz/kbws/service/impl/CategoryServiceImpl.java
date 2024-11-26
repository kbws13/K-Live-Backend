package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.mapper.CategoryMapper;
import xyz.kbws.model.entity.Category;
import xyz.kbws.service.CategoryService;

/**
* @author fangyuan
* @description 针对表【category(分类表)】的数据库操作Service实现
* @createDate 2024-11-27 00:11:01
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService {

}




