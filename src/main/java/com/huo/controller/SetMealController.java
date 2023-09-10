package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huo.common.GeneralResult;
import com.huo.dto.SetmealDto;
import com.huo.entity.Category;
import com.huo.entity.Setmeal;
import com.huo.service.CategoryService;
import com.huo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetmealService setMealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public GeneralResult<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息:{}",setmealDto);
        setMealService.saveWithDish(setmealDto);
        return GeneralResult.success("新增套餐成功");
    }





    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public GeneralResult<Page> page(int page,int pageSize, String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
//        添加套餐分类的展示 Page<SetmealDto> pageDto = new Page<>(page,pageSize);
        Page<SetmealDto> pageDto = new Page<>();

        //添加查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!= null, Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo,queryWrapper);

        //records携带所有信息
        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
//            dto比原表多了分类名称，即根据分类id获得分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            //对setmealDto进行除categoryName的属性进行拷贝(因为item里面没有categoryName)
            BeanUtils.copyProperties(item,setmealDto);

            return setmealDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);

        return GeneralResult.success(pageDto);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
  public GeneralResult<String> deleteByIds (@RequestParam List<Long> ids) {
        log.info("要删除的套餐Id为：{}",ids);
        setMealService.removeWithDish(ids);
        return GeneralResult.success("套餐删除成功");
  }


    /**
     * 套餐展示
     * @param setmeal
     * @return
     */
  @GetMapping("/list")
  public GeneralResult<List<Setmeal>> list (Setmeal setmeal){
      LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
      queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus, setmeal.getStatus());
      List<Setmeal> list = setMealService.list(queryWrapper);
      return GeneralResult.success(list);

  }
}

