package com.hopu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;

import java.util.ArrayList;

public interface IRoleService extends IService<Role> {
    void setMenu(String id, ArrayList<Menu> menus);

}
