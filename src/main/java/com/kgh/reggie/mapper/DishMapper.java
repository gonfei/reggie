package com.kgh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kgh.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
