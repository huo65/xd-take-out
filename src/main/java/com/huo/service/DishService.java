package com.huo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huo.dto.DishDto;
import com.huo.entity.Dish;

public interface DishService extends IService<Dish> {

//    新增菜品，同时插入口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);
//    更新菜品信息，同时更新口味
    public void updateWithFlavor(DishDto dishDto);
}
