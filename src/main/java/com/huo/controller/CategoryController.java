package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huo.common.GeneralResult;
import com.huo.entity.Category;
import com.huo.entity.Dish;
import com.huo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public GeneralResult<String> save(@RequestBody Category category) {
        log.info("category:{}",category);
        categoryService.save(category);
        return GeneralResult.success("添加分类成功");
    }
    /**
     * 菜品类分页
     */
    @GetMapping("/page")
    public GeneralResult<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);

        return GeneralResult.success(pageInfo);
    }
    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public GeneralResult<String> delete(@RequestParam("ids") Long id){
//        categoryService.removeById(id);
        categoryService.remove(id);
        return GeneralResult.success("删除分类成功");
    }

    /**
     * 根据id修改分类
     * @param category
     * @return
     */
    @PutMapping
    public GeneralResult<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return GeneralResult.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public GeneralResult<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
//        queryWrapper.eq(Category::,1);TODO 验证是否错误
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);


        List<Category> list = categoryService.list(queryWrapper);

        return GeneralResult.success(list);
    }
}
