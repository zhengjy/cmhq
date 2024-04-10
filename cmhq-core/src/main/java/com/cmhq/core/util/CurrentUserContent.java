package com.cmhq.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.model.FaCompanyEntity;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Jiyang.Zheng on 2024/4/9 12:25.
 */
public class CurrentUserContent {

    public static FaCompanyEntity getCurrentCompany() {
        Integer currentCompanyId = SecurityUtils.getCurrentCompanyId();
        FaCompanyDao faCompanyDao = SpringApplicationUtils.getBean(FaCompanyDao.class);
        FaCompanyEntity faCompanyEntity = faCompanyDao.selectById(currentCompanyId);
        return faCompanyEntity;
    }

    public static UserDto getCrrentUser(){
        UserDetails userDetails = SecurityUtils.getCurrentUser();
        // 将 Java 对象转换为 JSONObject 对象
        JSONObject jsonObject = (JSONObject) JSON.toJSON(userDetails);

        UserDto userDto = jsonObject.getObject("user",UserDto.class);
        return userDto;
    }

    /**
     * 是否商户
     * @return
     */
    public static boolean isCompanyUser(){
        UserDto userDto = getCrrentUser();
        if(CollectionUtils.isNotEmpty(userDto.getRoles())){
            Set<RoleSmallDto> roles=  userDto.getRoles();
            //2 商户,3:商户运维人员
            Optional<RoleSmallDto> role = roles.stream().filter(v -> v.getLevel().equals(2)).findFirst();;
            if (role.isPresent()){
                return true;
            }
        }
        return false;
    }
    /**
     * 是否商户子账户
     * @return
     */
    public static boolean isCompanyChildUser(){
        UserDto userDto = getCrrentUser();
        if(CollectionUtils.isNotEmpty(userDto.getRoles())){
                Set<RoleSmallDto> roles=  userDto.getRoles();
                //2 商户,3:商户运维人员
                Optional<RoleSmallDto> role = roles.stream().filter(v -> v.getLevel().equals(3)).findFirst();;
                if (role.isPresent()){
                    return true;
                }
        }
        return false;
    }
}
