package com.kgh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kgh.reggie.commons.R;
import com.kgh.reggie.entity.Employee;
import com.kgh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置初始密码
        String password = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
        employee.setPassword(password);
        employee.setStatus(1);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empID = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);
        log.info("新增员工信息：{}", employee.toString());
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //添加一个过滤条件
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, lqw);

        return R.success(pageInfo);
    }

    //    账户状态管理
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        Long empID = (Long) request.getSession().getAttribute("employee");

        employee.setUpdateUser(empID);
        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);
        return R.success("操作成功");
    }


    //修改员工信息

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息。。。。。");
        Employee employee = employeeService.getById(id);
        if (employee != null) {

            return R.success(employee);
        }
        return R.error("没有查到员工信息");
    }
}
