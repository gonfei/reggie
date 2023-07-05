package com.kgh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.entity.Category;
import com.kgh.reggie.entity.Employee;
import com.kgh.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/category")
@RestController
public class categoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> add(@RequestBody Category category) {
//        log.info("新增：{}", category);
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> category(int page, int pageSize) {
//        log.info("page:{},pageSize:{}", page, pageSize);
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        //添加排序条件
        lqw.orderByAsc(Category::getSort);

        //查询
        categoryService.page(pageInfo, lqw);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> del(Long id) {

        categoryService.removeById(id);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody  Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

}
