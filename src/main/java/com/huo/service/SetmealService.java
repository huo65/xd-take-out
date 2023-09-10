package com.huo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huo.dto.SetmealDto;
import com.huo.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

}
