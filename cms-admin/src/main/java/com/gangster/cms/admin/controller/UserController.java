package com.gangster.cms.admin.controller;

import com.gangster.cms.admin.annotation.SystemControllerLog;
import com.gangster.cms.admin.dto.AjaxData;
import com.gangster.cms.admin.dto.MessageDto;
import com.gangster.cms.admin.service.auth.UserConcreteService;
import com.gangster.cms.common.pojo.User;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Controller   与用户有关的所有操作
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserConcreteService userConcreteService;

    /**
     * 添加用户
     *
     * @param user 用户对象(用户信息)
     * @return Message 添加用户是否成功
     */
    @SystemControllerLog(description = "添加用户对象")
    @PostMapping
    @ResponseBody
    public MessageDto addUser(@RequestBody User user) {
        if (userConcreteService.addUser(user))
            return MessageDto.success(null);
        else return MessageDto.fail(1, "添加用户失败");
    }

    /**
     * 判断用户名是否重复
     *
     * @param userName 即将注册的新用户名
     * @return 重复信息
     */
    @SystemControllerLog(description = "判断用户名是否重复")
    @ResponseBody
    @GetMapping(value = "/judgeUserName/{userName}")
    public MessageDto judgeUserName(@PathVariable("userName") String userName) {
        if (!userConcreteService.judgeUserName(userName)) {
            if (userName.length() == 0) return MessageDto.fail(2, "用户名为空");
            return MessageDto.fail(1, "用户名已存在");
        } else return MessageDto.success("可以使用");
    }


    /**
     * 修改用户信息
     *
     * @param userid 用户Id
     * @param user   原始用户信息
     * @return Message  修改用户是否成功
     */
    @SystemControllerLog(description = "修改用户对象")
    @PutMapping(value = "{userid}")
    @ResponseBody
    public MessageDto updateUser(@PathVariable("userid") Integer userid, @RequestBody User user) {
        if (userConcreteService.updateUser(userid, user)) {
            return MessageDto.success(null);
        }
        return MessageDto.fail(1, "修改用户失败");
    }

    /**
     * 删除用户
     *
     * @param userId 用户的Id
     * @return int   删除用户数量
     */
    @SystemControllerLog(description = "删除用户对象")
    @DeleteMapping(value = "{UserId}")
    @ResponseBody
    public MessageDto deleteUser(@PathVariable("UserId") Integer userId) {
        if (userConcreteService.deleteSingleUser(userId)) {
            return MessageDto.success(null);
        }
        return MessageDto.fail(1, "删除对象失败");
    }

    /**
     * 查找所有的用户
     *
     * @param page  查找信息的页数
     * @param limit 每页所显示的条数
     * @return AjaxData 查找到的所有用户
     */
    @SystemControllerLog(description = "查找用户")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public AjaxData findUser(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "15") Integer limit) {
        PageInfo<User> pageInfo = userConcreteService.listAllUser(page, limit);
        if (null == pageInfo) {
            return new AjaxData(1, "failed", 0, null);
        }
        return new AjaxData(0, "success", pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 通过用户Id查找用户
     *
     * @param userId 用户的Id
     * @return User 查找到的用户
     */
    @SystemControllerLog(description = "查找单个用户")
    @GetMapping(value = "{UserId}")
    @ResponseBody
    public User fingUserById(@PathVariable("UserId") Integer userId) {
        return userConcreteService.findSingleUser(userId);
    }

    /**
     * 批量删除用户
     *
     * @param userIdData 用户的Id字符串
     * @return 删除信息
     */
    @SystemControllerLog(description = "批量删除用户")
    @PostMapping("/batchDeleting")
    @ResponseBody
    public MessageDto batchDeleting(String userIdData) {
        if (userConcreteService.batchDeleting(userIdData)) {
            return MessageDto.success(null);
        } else return MessageDto.fail(1, "批量删除失败");
    }
}
