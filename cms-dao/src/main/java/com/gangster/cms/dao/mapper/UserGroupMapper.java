package com.gangster.cms.dao.mapper;

import com.gangster.cms.common.pojo.UserGroup;
import com.gangster.cms.common.pojo.UserGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserGroupMapper {
    long countByExample(UserGroupExample example);

    int deleteByExample(UserGroupExample example);

    int insert(UserGroup record);

    int insertSelective(UserGroup record);

    List<UserGroup> selectByExample(UserGroupExample example);

    int updateByExampleSelective(@Param("record") UserGroup record, @Param("example") UserGroupExample example);

    int updateByExample(@Param("record") UserGroup record, @Param("example") UserGroupExample example);
}