package com.ganster.cms.core.service.impl;

import com.ganster.cms.core.base.impl.BaseServiceImpl;
import com.ganster.cms.core.dao.mapper.LogEntryMapper;
import com.ganster.cms.core.pojo.LogEntry;
import com.ganster.cms.core.pojo.LogEntryExample;
import com.ganster.cms.core.service.LogService;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends BaseServiceImpl<LogEntryMapper, LogEntry, LogEntryExample> implements LogService {
}