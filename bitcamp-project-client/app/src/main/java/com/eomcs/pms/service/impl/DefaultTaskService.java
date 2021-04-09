package com.eomcs.pms.service.impl;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import com.eomcs.pms.dao.TaskDao;
import com.eomcs.pms.domain.Task;
import com.eomcs.pms.service.TaskService;

public class DefaultTaskService implements TaskService {

  SqlSession sqlSession;

  TaskDao taskDao;

  public DefaultTaskService(SqlSession sqlSession, TaskDao taskDao) {
    this.sqlSession = sqlSession;
    this.taskDao = taskDao;
  }

  // 등록 업무
  @Override
  public int add(Task task) throws Exception {
    int count = taskDao.insert(task);
    sqlSession.commit();
    return count;
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
    int count = taskDao.update(task);
    sqlSession.commit();
    return count;
  }

  // 삭제 업무
  @Override
  public int delete(int no) throws Exception {
    int count = taskDao.delete(no);
    sqlSession.commit();
    return count;
  }

}

















