package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.xml.crypto.Data;
import java.awt.print.Pageable;

public interface XcTaskRepository extends JpaRepository<XcTask,String> {
    //查询某个时间之前的n条任务
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Data updateTime);
    //更新updateTime
    @Modifying
    @Query("update XcTask t set  t.updateTime= :updateTime where t.id= :id")
    public int updateTaskTime(@Param(value = "id") String id,@Param(value = "updateTime") Data updateTime);

    @Query("update XcTask t set  t.version= :version+1 where t.id= :id and t.version= :version")
    public int updateTaskVersion(@Param(value = "id")String  id,@Param(value = "int") int version );
}
