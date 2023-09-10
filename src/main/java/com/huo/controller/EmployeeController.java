package com.huo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huo.common.GeneralResult;
import com.huo.entity.Employee;
import com.huo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j//方便以后调试
@RestController
@RequestMapping("/employee")

public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public GeneralResult<Employee> login(HttpServletRequest request, @RequestBody  Employee employee){
//        1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        3、如果没有查询到则返回登录失败结果
        if (emp == null){
            return GeneralResult.error("用户不存在");
        }
//        4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return GeneralResult.error("密码错误");
        }
//        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return GeneralResult.error("账号已禁用");
        }
//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
//        一直返回登录页的注意下request.getSession().setAttribute("employee", emp.getId());
        log.info("登录成功");
        return GeneralResult.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public GeneralResult<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return GeneralResult.success("退出成功");
    }

    @PostMapping()
    public GeneralResult<String> save(HttpServletRequest request,@RequestBody Employee employee) {
//        设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId =(Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return GeneralResult.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public GeneralResult<Page> page(int page, int pageSize,String name){

//        构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

//        条件构造
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        自带检查，无需if
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询  这里不用封装了mybatis-plus帮我们做好了
        employeeService.page(pageInfo,queryWrapper);

        return GeneralResult.success(pageInfo);

    }
    @PutMapping
    public GeneralResult<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

//        Long empId =(Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return GeneralResult.success("员工信息修改成功");
    }

    /**
     * 根据前端传过来的员工id查询数据库进行数据会显给前端
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public GeneralResult<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return GeneralResult.success(employee) ;
        }
        return GeneralResult.error("没有查询到该员工信息");

    }


}


