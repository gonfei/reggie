package com.kgh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kgh.reggie.commons.BaseContext;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.entity.ShoppingCart;
import com.kgh.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车展示
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();

        //用户查询
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getid());

        //排序
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        //购物车内容查询
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        // 指定用户id
        Long getid = BaseContext.getid();
        shoppingCart.setUserId(getid);

        //查询菜品或套餐是否存在在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, getid);

        if (dishId != null) {
            //不为空，则添加的是菜品
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //为空，添加的是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(wrapper);

        //如果已存在，则购物车内容数量+1
        if (one != null) {
            Integer number = one.getNumber();//购物车已存数量
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            //不存在，则加入购物车
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

}
