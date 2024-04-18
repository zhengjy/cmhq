package com.cmhq.core.service.impl;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.HomepageService;
import com.cmhq.core.util.CurrentUserContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Jiyang.Zheng on 2024/4/18 15:38.
 */
@Service
public class HomepageServiceImpl implements HomepageService {
    @Autowired
    private FaCompanyService faCompanyService;

    @Override
    public Object getCompanyHome() {
        FaCompanyEntity entity = CurrentUserContent.getCurrentCompany();
        return null;
    }
}
