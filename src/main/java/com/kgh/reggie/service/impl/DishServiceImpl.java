package com.kgh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kgh.reggie.dto.DishDto;
import com.kgh.reggie.entity.Dish;
import com.kgh.reggie.entity.DishFlavor;
import com.kgh.reggie.mapper.DishMapper;
import com.kgh.reggie.service.DishFlavorService;
import com.kgh.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品同时保存口味数据
    @Transactional //事务注解
    public void saveWithDishFlavor(DishDto dishDto) {
        //保存基本信息到dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据dish_flavor

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish byId = this.getById(id);//查询菜品基本信息

        //对象copy
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(byId, dishDto);

        //查询当前菜品对应口味信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, byId.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);//口味
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithDishFlavor(DishDto dto) {
        //更新dish表
        this.updateById(dto);

        //清除口味表
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dto.getId());
        dishFlavorService.remove(wrapper);

        //更新口味表
        List<DishFlavor> flavors = dto.getFlavors();

        //新增口味需要重新传入dishid
        flavors = flavors.stream().map(item -> {
            item.setDishId(dto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }



}
