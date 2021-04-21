package com.eomcs.pms.service.impl;

import java.util.List;
import com.eomcs.pms.dao.TaskDao;
import com.eomcs.pms.domain.Task;
import com.eomcs.pms.service.TaskService;

public class DefaultTaskService implements TaskService {

  TaskDao taskDao; 

  public DefaultTaskService(TaskDao taskDao) {
    this.taskDao = taskDao;
  }

  // 등록 업무
  @Override
  public int add(Task task) throws Exception {
    return taskDao.insert(task);
  }

  // 조회 업무
  @Override
  public List<Task> list() throws Exception {
    return taskDao.findAll();
  }

  @Override
  public List<Task> listOfProject(int projectNo) throws Exception {
    return taskDao.findByProjectNo(projectNo);
  }

  // 상세 조회 업무
  @Override
  public Task get(int no) throws Exception {
    return taskDao.findByNo(no);
  }

  // 변경 업무
  @Override
  public int update(Task task) throws Exception {
    return taskDao.update(task);
  }

  // 삭제 업무
  @Override
  public int delete(int no) throws Exception {
    return taskDao.delete(no);
  }

}







