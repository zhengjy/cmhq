package com.cmhq.core.util;

import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.model.FaCompanyEntity;
import me.zhengjie.utils.SecurityUtils;

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
}
