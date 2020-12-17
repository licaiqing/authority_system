package com.hopu.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hopu.domain.Menu;

import com.hopu.domain.RoleMenu;
import com.hopu.result.PageEntity;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IMenuService;
import com.hopu.service.IRoleMenuService;
import com.hopu.utils.IconFontUtils;
import com.hopu.utils.UUIDUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRoleMenuService roleMenuService;

    //向菜单列表跳转
    @GetMapping("/toMenuPage")
    public String toListPage(){
        return "admin/menu/menu_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    @RequiresPermissions("menu:list")
    public PageEntity list(){

        //先查询父菜单

        List<Menu> pList=menuService.list(new QueryWrapper<Menu>().eq("pid","0"));
        //接着查询对应的所有子菜单，把子菜单分装到父菜单对象的属性nodes中

        //需求：返回各个菜单集合
        ArrayList<Menu> menus=new ArrayList<>();

        findChildMenu(pList, menus);

        //return new PageEntity(menuList.size(),menuList);
        return new PageEntity(menus.size(),menus);
        // 如果不涉及到子菜单关联
        // List<Menu> list = menuService.list();
        // return new PageEntity(list.size(),list);

    }

    //私有方法循环查询儿子菜单列表
    private List<Menu> findChildMenu(List<Menu> pList,List<Menu> menus){
/*        pList.forEach(menu -> {
            menus.add(menu);// 向返回集合中，添加父菜单
            String pId=menu.getId();
            List<Menu> childList=menuService.list(new QueryWrapper<Menu>().eq("pid",pId));
            menu.setNodes(childList);
            menus.addAll(childList);//向返回集合中，添加子菜单
            //需要在子菜单基础上，继续遍历递归查询孙子菜单
           *//* childList.forEach(menul -> {
                String childId = menul.getId();
                List<Menu> grandsonList = menuService.list(new QueryWrapper<Menu>().eq("pid", childId));
                menul.setNodes(grandsonList);*//*
            if(childList.size()>0){
                // 递归调用
                menus= findChildMenu(childList,menus);

            }
        });*/
        for (Menu menu : pList) {
            if(!menus.contains(menu)){
                menus.add(menu);
            }
            String pId = menu.getId();
            List<Menu> childList = menuService.list(new QueryWrapper<Menu>().eq("pid", pId));
            menu.setNodes(childList);
            if(childList.size()>0){
                // 递归调用
                menus= findChildMenu(childList,menus);
            }
        }
        return menus;
    }

    //向添加页面跳转
    @RequestMapping("/toAddPage")
    @RequiresPermissions("menu:save")
    public String toAddPage(Model model){
        //父级菜单
        List<Menu> list=menuService.list(new QueryWrapper<>(new Menu()).eq("pid",'0'));
        findChildrens(list);
        //图标
        List<String> iconFont= IconFontUtils.getIconFont();

        model.addAttribute("iconFont",iconFont);
        model.addAttribute("list",list);
        return "admin/menu/menu_add";
    }
    private void findChildrens(List<Menu> list){
        for (Menu menu : list) {
            List<Menu> list2 = menuService.list(new
                    QueryWrapper<Menu>(new Menu()).eq("pid", menu.getId()));
            if (list2!=null) {
                menu.setNodes(list2);
            }
        }
    }

/*
   // 向菜单添加页面跳转
   @RequestMapping("/toAddPage")
   public String toAddPage(HttpServletRequest request){
       // 查询父级目录（不需要查询第三级菜单）
       // 先查询顶级父目录
       List<Menu> pMenus = menuService.list(new QueryWrapper<Menu>().eq("pid", "0"));
       // 查询并封装对应的子菜单
       pMenus.forEach(menu -> {
           List<Menu> childMenus = menuService.list(new QueryWrapper<Menu>().eq("pid", menu.getId()));
           menu.setNodes(childMenus);
       });

       //查询所有字体图标(查询所有字体图片class类)
       List<String> iconFont = IconFontUtils.getIconFont();

       request.setAttribute("list",pMenus);
       request.setAttribute("iconFont",iconFont);

       return "admin/menu/menu_add";
   }*/


    //保存
    @ResponseBody
    @RequestMapping("/save")
    public ResponseEntity addMenu(Menu menu){
        menu.setId(UUIDUtils.getID());
        menu.setCreateTime(new Date());
        menuService.save(menu);
        return ResponseEntity.success();
    }

    //跳转修改页面
    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("menu:update")
    public String toUpdatePage(String id,Model model) {
        Menu menu = menuService.getById(id);
        model.addAttribute("menu", menu);

        List<Menu> list = menuService.list(new
                QueryWrapper<Menu>(new Menu()).eq("pid", '0').orderByAsc("seq"));
        findChildrens(list);
        //图标
        List<String> iconFont = IconFontUtils.getIconFont();

        model.addAttribute("iconFont", iconFont);
        model.addAttribute("list", list);
        return "admin/menu/menu_update";
    }

    //修改
    @ResponseBody
    @RequestMapping("update")
    public ResponseEntity updateMenu(Menu menu){
        menu.setUpdateTime(new Date());
        menuService.updateById(menu);
        return ResponseEntity.success();
    }

    //删除
    @ResponseBody
    @RequestMapping("/delete")
    @RequiresPermissions("menu:delete")
    public ResponseEntity delete(@RequestBody ArrayList<Menu>
                                         menus){
        List<String> list = new ArrayList<String>();
        for (Menu menu : menus) {
            list.add(menu.getId());
        }
        menuService.removeByIds(list);
        return ResponseEntity.success();
    }

    @RequestMapping("/MenuList")
    @ResponseBody
    public PageEntity menuList(String roleId){
        // 查询当前角色已经关联了的权限
        List<RoleMenu> roleMenuList = roleMenuService.list(new
                QueryWrapper<RoleMenu>().eq("role_id", roleId));
        // 如果不涉及到子菜单关联
        List<Menu> list = menuService.list();

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        list.forEach(menu -> {
            // 先需要把对象转换为JSON格式
            JSONObject jsonObject =
                    JSONObject.parseObject(JSONObject.toJSONString(menu));
            // 判断是否已经有了对应的权限
            List<String> menuIds = roleMenuList.stream().map(roleMenu -> roleMenu.getMenuId()).collect(Collectors.toList());
            if(menuIds.contains(menu.getId())){
                jsonObject.put("LAY_CHECKED",true);
            }
            jsonObjects.add(jsonObject);
        });
        return new PageEntity(jsonObjects.size(),jsonObjects);
    }
}
