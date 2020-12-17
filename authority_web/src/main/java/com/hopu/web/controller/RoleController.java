package com.hopu.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;
import com.hopu.domain.UserRole;
import com.hopu.result.PageEntity;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IRoleService;
import com.hopu.service.IUserRoleService;
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
@RequestMapping("role")
public class RoleController {
    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserRoleService userRoleService;

    //跳转分配权限页面
    @RequestMapping("/toSetMenuPage")
    public String toSetMenuPage(Model model,String id){

        model.addAttribute("role_id",id);
        return "admin/role/role_setMenu";
    }

    //设置权限
    @ResponseBody
    @RequestMapping("setMenu")
    public ResponseEntity setMenu(String id,@RequestBody ArrayList<Menu>
            menus){
        roleService.setMenu(id,menus);
        return ResponseEntity.success();
    }


     //查询用户关联的角色列表
    @ResponseBody
    @RequestMapping("/roleList")

    public PageEntity List(String userId, Role role){
        List<UserRole> userRoles = userRoleService.list(new
                QueryWrapper<UserRole>().eq("user_id", userId));
        QueryWrapper<Role> queryWrapper = new QueryWrapper<Role>();
        if (role!=null){
            if (!StringUtils.isEmpty(role.getRole()))
                queryWrapper.like("role", role.getRole());
        }
        List<Role> roles = roleService.list(queryWrapper);
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (Role role2 : roles) {
            JSONObject jsonObject =
                    JSONObject.parseObject(JSONObject.toJSONString(role2));
            boolean rs = false;
            for (UserRole userRole : userRoles) {
                if (userRole.getRoleId().equals(role2.getId())) {
                    rs = true;

                }
            }
            jsonObject.put("LAY_CHECKED", rs);
            list.add(jsonObject);
        }
        return new PageEntity(list.size(), list);
    }


    //视图页面跳转,先进入角色列表页面
    @GetMapping("/torolelistPage")
    public String toRoleListPage(){
        return "admin/role/role_list";
    }

    @GetMapping("/list")
    @ResponseBody
    @RequiresPermissions("role:list")
    public IPage<Role> findByPage(@RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "limit",defaultValue = "5") Integer pageSize,
                                  Role role, HttpServletRequest request){
        //增强分页处理
        Page<Role> page=new Page<>(pageNum,pageSize);

        //
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>(new Role());
        if(role !=null){
            if(!StringUtils.isEmpty(role.getRole()))
                queryWrapper.like("role",role.getRole());
            if(!StringUtils.isEmpty(role.getRemark()))
                queryWrapper.like("remark",role.getRemark());

        }

        //查询时带上条件
        IPage<Role> iPage=roleService.page(page,queryWrapper);
        return iPage;
    }

    //向添加页面跳转
    @RequestMapping("/toAddPage")
    @RequiresPermissions("role:add")
    public String toAddPage(){
        return "admin/role/role_add";
    }

    //异步添加角色
    @ResponseBody
    @RequestMapping("/add")

    public ResponseEntity addRole(Role role){

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",role.getRole());
        Role one=roleService.getOne(queryWrapper);

        if(one !=null){
            return ResponseEntity.error("角色名已经存在了");
        }

        //开始添加角色
        role.setId(UUIDUtils.getID());
        role.setRemark(UUIDUtils.getID());
        role.setCreateTime(new Date());

        roleService.save(role);
        return ResponseEntity.success();
    }

    // 向修改页面跳转
    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("role:update")
    public String toUpdatePage(String id,HttpServletRequest request){
        Role role = roleService.getById(id);
        request.setAttribute("role",role);
        return "admin/role/role_update";
    }

    // 角色修改
    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity updateRole(Role role){
        role.setUpdateTime(new Date());
        roleService.updateById(role);
        return ResponseEntity.success();
    }

    //角色删除
    @RequestMapping("/delete")
    @ResponseBody
    @RequiresPermissions("role:delete")
    public ResponseEntity deleteRole(@RequestBody List<Role> roles){
        try {
            // 如果是root角色，禁止删除
            for (Role role : roles) {
                if("root".equals(role.getRole())){
                    throw new Exception("不能删除超级角色");
                }
 //               if(role.getRole().equals("root")){ // nullpointerException
//                    throw new Exception("不能删除超级管理员");
//                }
                roleService.removeById(role.getId());
            }
            return ResponseEntity.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.error(e.getMessage());
        }
    }


}
