package com.kgh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kgh.reggie.dto.SetmealDto;
import com.kgh.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    //保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    void removeWithSetmealDishById(String id);

    SetmealDto getWithDIshById(long id);

    void updateWithDish(SetmealDto setmealDto);
}
