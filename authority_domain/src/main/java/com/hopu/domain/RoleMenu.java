package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单表
 */
@Data
@TableName("t_role_menu")
public class RoleMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    private String menuId;  // 菜单id
    private String roleId; // 角色id


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RoleMenu{" +
                "menuId='" + menuId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
