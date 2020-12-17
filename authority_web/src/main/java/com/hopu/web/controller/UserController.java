package com.hopu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Role;
import com.hopu.domain.User;

import com.hopu.result.ResponseEntity;
import com.hopu.service.IUserService;


import com.hopu.utils.ShiroUtils;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    //跳转分配角色界面
    @RequestMapping("/toSetRole")
    public String toSetRole(String id,Model model){
        model.addAttribute("user_id",id);
        return "admin/user/user_setRole";
    }
    //设置角色
    @RequestMapping("setRole")
    @ResponseBody
    public ResponseEntity setRole(String id, @RequestBody ArrayList<Role> roles){
        userService.setRole(id,roles);
        return ResponseEntity.success();
    }

   /* @RequestMapping("/user/list")
    public ResponseEntity<List<User>> list(){
        List<User> list = userService.list();
        return new ResponseEntity<List<User>>(list,HttpStatus.FOUND);
    }*/

   //视图页面跳转,先进入用户列表页面
    @RequiresPermissions("user:list")
    @GetMapping("/tolistPage")
    public String toUserListPage(){
        return "admin/user/user_list";
    }

   @GetMapping("/list")
   @ResponseBody
   public IPage<User> findByPage(@RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "limit",defaultValue = "5") Integer pageSize,
                                 User user,HttpServletRequest request){

   //public IPage<User> findByPage(int pageNum, int pageSize, User user, Model model){

       //PageInfo<User> pageInfo=userService.findByPage(pageNum,pageSize);
       //使用mybatis-plus增强分页处理
       Page<User> page=new Page<User>(pageNum,pageSize);

       // QueryWrapper封装查询条件
       QueryWrapper<User> queryWrapper = new QueryWrapper<>(new User());
       if (user !=null){
           if (!StringUtils.isEmpty(user.getUserName()))
               queryWrapper.like("user_name", user.getUserName());
           if (!StringUtils.isEmpty(user.getTel()))
               queryWrapper.like("tel", user.getTel());
           if (!StringUtils.isEmpty(user.getEmail()))
               queryWrapper.like("email", user.getEmail());
       }

       //分页查询时，带上分页数据以及查询条件对象
       IPage<User> iPage=userService.page(page,queryWrapper);

       //request.setAttribute("page",iPage);
       //return "admin/user/user_list";
       return iPage;
   }

   //向添加页面跳转
    @RequestMapping("/toAddPage")
    @RequiresPermissions("user:add")
    public String toAddPage(){
        return "admin/user/user_add";
    }

    //异步添加用户
    @ResponseBody
    @RequestMapping("/add")
    public ResponseEntity addUser(User user){

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        User one=userService.getOne(queryWrapper);

        if(one !=null){
            return ResponseEntity.error("用户名已经存在了");
        }

        //开始添加用户
        user.setId(UUIDUtils.getID());
        user.setSalt(UUIDUtils.getID());

        ShiroUtils.encPass(user);

        user.setCreateTime(new Date());

        userService.save(user);
        return ResponseEntity.success();
    }

    // 向修改页面跳转
    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("user:update")
    public String toUpdatePage(String id,HttpServletRequest request){
        User user = userService.getById(id);
        request.setAttribute("user",user);
        return "admin/user/user_update";
    }

    // 用户修改
    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity updateUser(User user){

        ShiroUtils.encPass(user);

        user.setUpdateTime(new Date());
        userService.updateById(user);
        return ResponseEntity.success();
    }

    // 用户删除
    @RequestMapping("/delete")
    @RequiresPermissions("user:delete")
    @ResponseBody
    public ResponseEntity deleteUser(@RequestBody List<User> users){
        try {
            // 如果是root用户，禁止删除
            for (User user : users) {
                if("root".equals(user.getUserName())){
                    throw new Exception("不能删除超级管理员");
                }
//                if(user.getUserName().equals("root")){ // nullpointerException
//                    throw new Exception("不能删除超级管理员");
//                }
                userService.removeById(user.getId());
            }
            return ResponseEntity.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.error(e.getMessage());
        }
    }

}
