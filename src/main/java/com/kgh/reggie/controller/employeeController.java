package com.kgh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.entity.Employee;
import com.kgh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class employeeController {
//controller->service->mapper->db

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

//1.将页面提交的密码进行md5加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes(StandardCharsets.UTF_8));

//2.根据用户提交的用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);//数据库查出的对象

//3.如果没有查到 返回登录失败的结果
        if (one == null) {
            return R.error("用户名错误！");
        }

//4.密码比对
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误，登录失败！");

        }
//5.查看用户状态
        if (one.getStatus().equals(0)) {
            return R.error("账户状态异常，无法登录！");
        }

//6.登录，将员工id存入Session并返回成功的结果
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除Session中存放员工的id
        request.getSession().removeAttribute("employee");
        return R.success("成功退出!");

    }
}
