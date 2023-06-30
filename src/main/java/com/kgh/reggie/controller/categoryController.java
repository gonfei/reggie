package com.kgh.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/category")
@RestController
public class categoryController {

    @GetMapping("/page")
    public String category(int page,int pageSize){
        log.info("page:{},pageSize:{}", page, pageSize);


        return null;
    }
}
