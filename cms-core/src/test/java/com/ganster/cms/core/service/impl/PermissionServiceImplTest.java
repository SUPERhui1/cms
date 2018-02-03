package com.ganster.cms.core.service.impl;

import com.ganster.cms.core.CmsConst;
import com.ganster.cms.core.CoreApplication;
import com.ganster.cms.core.exception.GroupNotFountException;
import com.ganster.cms.core.exception.UserNotFoundException;
import com.ganster.cms.core.pojo.Group;
import com.ganster.cms.core.pojo.Site;
import com.ganster.cms.core.pojo.User;
import com.ganster.cms.core.service.GroupService;
import com.ganster.cms.core.service.PermissionService;
import com.ganster.cms.core.service.SiteService;
import com.ganster.cms.core.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreApplication.class)
public class PermissionServiceImplTest {
    @Autowired
    UserService userService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    GroupService groupService;

    @Autowired
    SiteService siteService;

    @Test
    public void interTest() {
        final String userName = "@#$%^&";
        final String siteName = "$%^&&";
        User user = new User();
        user.setUserName(userName);
        userService.insert(user);

        Site site = new Site();
        site.setSiteName(siteName);
        siteService.insert(site);

        Group group = new Group();
        group.setGroupName(userName);
        groupService.insert(group);
        System.out.println(group.getGroupId());
        try {
            groupService.addUserToGroup(user.getUserId(), group.getGroupId());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (GroupNotFountException e) {
            e.printStackTrace();
        }

        try {
            permissionService.addUserToSite(user.getUserId(), site.getSiteId());
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (GroupNotFountException e) {
            e.printStackTrace();
        }

        try {
            List<Site> siteList = permissionService.findAllUserSite(user.getUserId());
            System.out.println(siteList);
        } catch (GroupNotFountException e) {
            e.printStackTrace();
        }

        try {
            permissionService.addPermissionToUser(user.getUserId(), site.getSiteId(), 3, CmsConst.PERMISSION_VIEW);
        } catch (GroupNotFountException e) {
            e.printStackTrace();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        boolean hasP = permissionService.hasPermission(user.getUserId(), site.getSiteId(), 3, CmsConst.PERMISSION_VIEW);
        Assert.assertTrue(hasP);
    }

}