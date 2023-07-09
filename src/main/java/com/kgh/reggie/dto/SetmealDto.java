package com.kgh.reggie.dto;

import com.kgh.reggie.entity.Setmeal;
import com.kgh.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
