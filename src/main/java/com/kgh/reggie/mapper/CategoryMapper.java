package com.kgh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kgh.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
