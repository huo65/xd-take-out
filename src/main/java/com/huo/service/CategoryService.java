package com.huo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huo.common.GeneralResult;
import com.huo.entity.Category;

public interface CategoryService extends IService<Category> {

    public GeneralResult<String> remove(Long id);
}
