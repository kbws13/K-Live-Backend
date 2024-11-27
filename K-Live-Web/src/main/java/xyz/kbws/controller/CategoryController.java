package xyz.kbws.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.entity.Category;
import xyz.kbws.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/27
 * @description:
 */
@Api(tags = "分类接口")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "获取所有分类")
    @PostMapping("/query")
    public BaseResponse<List<Category>> getAllCategory() {
        List<Category> list = categoryService.getAllCategory();
        return ResultUtils.success(list);
    }
}
