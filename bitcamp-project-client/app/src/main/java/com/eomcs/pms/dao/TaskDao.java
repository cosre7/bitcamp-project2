package com.eomcs.pms.dao;

import java.util.List;
import com.eomcs.pms.domain.Task;

public interface TaskDao {

  int insert(Task task) throws Exception;

  List<Task> findAll() throws Exception ;

  List<Task> findByProjectNo(int projectNo) throws Exception;

  Task findByNo(int no) throws Exception;

  int update(Task task) throws Exception;

  int delete(int no) throws Exception;

  ////기존의 프로그램에 영향을 주지 않으면서 메서드를 추가하기 위한 문법
  //  default int deleteByProjectNo(int projectNo) throws Exception {return 0;}; 

  int deleteByProjectNo(int projectNo) throws Exception;
}