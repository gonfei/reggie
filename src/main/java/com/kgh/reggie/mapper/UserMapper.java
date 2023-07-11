package com.kgh.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kgh.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
