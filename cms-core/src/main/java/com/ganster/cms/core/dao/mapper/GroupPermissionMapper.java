package com.ganster.cms.core.dao.mapper;

import com.ganster.cms.core.pojo.GroupPermission;
import com.ganster.cms.core.pojo.GroupPermissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GroupPermissionMapper {
    long countByExample(GroupPermissionExample example);

    int deleteByExample(GroupPermissionExample example);

    int insert(GroupPermission record);

    int insertSelective(GroupPermission record);

    List<GroupPermission> selectByExample(GroupPermissionExample example);

    int updateByExampleSelective(@Param("record") GroupPermission record, @Param("example") GroupPermissionExample example);

    int updateByExample(@Param("record") GroupPermission record, @Param("example") GroupPermissionExample example);
}