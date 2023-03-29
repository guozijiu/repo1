package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
        @Autowired
    XcUserRepository xcUserRepository;
        @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;

        //根据账号查询XcUser信息
    public XcUser findXcUserByUsername(String username){
        return xcUserRepository.findByUsername(username);
    }
        //根据账号查询用户信息
    public XcUserExt getUserExt(String username){
        //根据用户账号查询xcUser信息
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser==null){
            return null;
        }
        //用户id获取
        String userId = xcUser.getId();
        //根据用户查询所属公司id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByuserId(userId);
        //取到用户的公司id
        String companyId=null;
        if (xcCompanyUser!=null){
             companyId = xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);
        return xcUserExt;

    }
}
