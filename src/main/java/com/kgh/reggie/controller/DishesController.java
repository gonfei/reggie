package com.kgh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.dto.DishDto;
import com.kgh.reggie.entity.Category;
import com.kgh.reggie.entity.Dish;
import com.kgh.reggie.entity.DishFlavor;
import com.kgh.reggie.service.CategoryService;
import com.kgh.reggie.service.DishFlavorService;
import com.kgh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishesController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 添加菜品
     *
     * @param dto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dto) {
        dishService.saveWithDishFlavor(dto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //1.分页构造器对象 2.条件

        //1
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> page1 = new Page<>();


        //2
        //条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //添加条件
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序
        wrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, page1, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);//copy

            Long categoryId = item.getCategoryId();//分类id
            Category byId = categoryService.getById(categoryId);//根据id查询分类对象
            if (byId != null) {
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;
        }).collect(Collectors.toList());


        page1.setRecords(list);
        return R.success(page1);
    }


    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dto) {
        dishService.updateWithDishFlavor(dto);
        return R.success("添加成功");
    }

    //删除（支持批量）
    @DeleteMapping
    public R<String> del(String[] ids) {
        for (String id : ids) {
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

    //起售停售（支持批量）
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, String[] ids) {
        for (String id : ids) {
            Dish dish = dishService.getById(id);//获得菜品
            dish.setStatus(status);//根据前端传入的status参数设置菜品状态
            dishService.updateById(dish);//更新
        }
        return R.success("操作成功");
    }
}
