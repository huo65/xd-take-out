package com.huo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huo.common.CustomException;
import com.huo.common.GeneralResult;
import com.huo.entity.Category;
import com.huo.entity.Dish;
import com.huo.entity.Setmeal;
import com.huo.mapper.CategoryMapper;
import com.huo.service.CategoryService;
import com.huo.service.DishService;
import com.huo.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setMealService;

    /**
     * 根据id删除分类，删除之前需要进行判断是否有关联数据
     * @return
     */
    @Override
    public GeneralResult<String> remove(Long id) {
//        添加查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealLambdaQueryWrapper);

//        检查是否关联菜品，如果关联抛出业务异常
        if (count1>0){
            throw new  CustomException("当前分类项关联了菜品,不能删除");
        }
//        检查是否关联套餐，如果关联抛出业务异常
        if (count2>0){
            throw new  CustomException("当前分类项关联了套餐,不能删除");
        }

//        正常删除
        super.removeById(id);
        return null;
    }
}
