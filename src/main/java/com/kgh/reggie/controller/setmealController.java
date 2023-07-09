package com.kgh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.dto.SetmealDto;
import com.kgh.reggie.entity.Category;
import com.kgh.reggie.entity.Setmeal;
import com.kgh.reggie.service.CategoryService;
import com.kgh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class setmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pages(int page, int pageSize, String name) {
        //分页 条件
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        Page<SetmealDto> pageTemp = new Page<>();


        //条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);

        setmealService.page(pageInfo, wrapper);

        //对象copy，显示套餐分类
        BeanUtils.copyProperties(pageInfo, pageTemp, "records");

        List<Setmeal> records = pageInfo.getRecords();

        //item表示遍历出来的Setmeal
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto dto = new SetmealDto();//新建dto对象

            //将item拷贝到dto对象，此时dto对象的categoryName属性为空
            BeanUtils.copyProperties(item, dto);

            Long categoryId = item.getCategoryId();//拿到菜品种类id
            Category byId = categoryService.getById(categoryId);//拿到菜品种类对象

            if (byId != null) {
                String name1 = byId.getName();//拿到菜品对象的name属性
                dto.setCategoryName(name1);//给dto对象赋值
            }
            return dto;
        }).collect(Collectors.toList());

        pageTemp.setRecords(list);

        return R.success(pageTemp);
    }

    /**
     * 删除套餐（支持批量删除）
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> del(String[] ids) {
        for (String id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal.getStatus() == 1) {
                return R.error(setmeal.getName() + "：正在售卖中！不能删除");
            }
            setmealService.removeWithSetmealDishById(id);
        }
        return R.success("删除成功");
    }

    /**
     * 套餐启停售（支持批量）
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, String[] ids) {
        for (String id : ids) {
            Setmeal setmeal = setmealService.getById(id);//获取套餐
            setmeal.setStatus(status);//设置套餐状态
            setmealService.updateById(setmeal);//更新套餐状态
        }
        return R.success("更改状态成功");
    }

    /**
     * 套餐回显
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable long id){
        return R.success(setmealService.getWithDIshById(id));
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("更新成功");
    }
}
