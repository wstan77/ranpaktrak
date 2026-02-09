package com.lbisoft.core.app.db.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lbisoft.core.app.components.thread.model.MessageThread;

@Mapper
public interface ThreadExtendedMapper {

  List<MessageThread> getThreadsByReference(@Param("referenceTypeLk") String referenceTypeLk,
      @Param("referenceId") Integer referenceId, @Param("userId") Integer userId);

  List<MessageThread> getThreadsByParticipant(@Param("userId") Integer userId);

  List<MessageThread> getThreadsByIds(@Param("threadIds") List<Integer> threadIds, @Param("userId") Integer userId);
}
