package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huo.common.GeneralResult;
import com.huo.dto.DishDto;
import com.huo.entity.Category;
import com.huo.entity.Dish;
import com.huo.entity.DishFlavor;
import com.huo.service.CategoryService;
import com.huo.service.DishFlavorService;
import com.huo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public GeneralResult<String> save(@RequestBody DishDto dishDto) {
        String key = "dish_" + dishDto.getCategoryId() +"_"+ dishDto.getStatus();
        redisTemplate.delete(key);
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return GeneralResult.success("新增菜品成功");
    }

    /**
     * 菜品的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public GeneralResult<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

//        对象拷贝(records为查询到的数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return GeneralResult.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public GeneralResult<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return GeneralResult.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public GeneralResult<String> update(@RequestBody DishDto dishDto) {
        String key = "dish_" + dishDto.getCategoryId() +"_"+ dishDto.getStatus();
        redisTemplate.delete(key);
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return GeneralResult.success("新增菜品成功");
    }

    /**
     * 根据条件查询菜品
      * @param dish
     * @return
     */
    @GetMapping("/list")
    public GeneralResult<List<DishDto>> list (Dish dish) {

//        动态构造key
        String key = "dish_" + dish.getCategoryId() +"_"+ dish.getStatus();
        List<DishDto>  dishDtoListCache= (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoListCache != null){
            return GeneralResult.success(dishDtoListCache);
        }
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
//        添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

//        将结果转换为dishDto +口味和分类名称
        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(dishId != null,DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }) .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return GeneralResult.success(dishDtoList);
    }
}
