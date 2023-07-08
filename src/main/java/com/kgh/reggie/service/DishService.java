package com.kgh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kgh.reggie.dto.DishDto;
import com.kgh.reggie.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    //新增菜品，同时插入口味数据，需要操作两张表 dish，dish_flavor
    public void saveWithDishFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    void updateWithDishFlavor(DishDto dto);


}
