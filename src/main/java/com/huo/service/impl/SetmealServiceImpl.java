package com.huo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huo.common.CustomException;
import com.huo.dto.SetmealDto;
import com.huo.entity.Setmeal;
import com.huo.entity.SetmealDish;
import com.huo.mapper.SetmealMapper;
import com.huo.service.SetmealService;
import com.huo.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增菜品，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(@RequestBody SetmealDto setmealDto) {
//        保存套餐的基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId((setmealDto.getId()));
            return item;
        }).collect(Collectors.toList());

//        保存套餐和菜品的关联信息，操作setmeal，dish
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        套餐在售的话即status为1，不能删
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setMealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(setMealLambdaQueryWrapper);

        if (count>0) {
            throw new CustomException("套餐正在售卖中，请先停售再进行删除");
        }
//      如果可以删除，先删除套餐表的数据--setmeal
        this.removeByIds(ids);
//        再删除关系表中的数据--setmealDish
//        delete from setmeal_dish where setmeal_id in(1,2,3)
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

//        setmealDishService  ids:setmealID
        setmealDishService.remove(dishLambdaQueryWrapper);

    }
}