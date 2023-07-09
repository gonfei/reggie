package com.kgh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kgh.reggie.dto.SetmealDto;
import com.kgh.reggie.entity.Dish;
import com.kgh.reggie.entity.Setmeal;
import com.kgh.reggie.entity.SetmealDish;
import com.kgh.reggie.mapper.SetmealMapper;
import com.kgh.reggie.service.SetmealDishService;
import com.kgh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    @Override
    public void removeWithSetmealDishById(String id) {
        this.removeById(id);
        //删除setmeal_dish表响应内容
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(wrapper);
    }

    @Override
    public SetmealDto getWithDIshById(long id) {
        Setmeal setmeal = this.getById(id);//拿到当前套餐基本信息

        SetmealDto dto = new SetmealDto();

        //setmeal对象拷贝到dto
        BeanUtils.copyProperties(setmeal, dto);

        //拿到当前套餐对应的菜品信息
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(wrapper);

        dto.setSetmealDishes(list);
        return dto;
    }

    /**
     * 更新套餐
     *
     * @param setmealDto
     * @return
     */
    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);//更新套餐基本表

        //清除菜品表
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(wrapper);

        //更新菜品表
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map(item -> {
            //新传入菜品添加套餐id即 setmealId
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }


    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //插入套餐基本信息 操作setmeal表
        this.save(setmealDto);

        //保存套餐个菜品的关联关系，操作setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置套餐id
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


}
