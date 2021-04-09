package com.eomcs.pms.service;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import com.eomcs.pms.dao.ProjectDao;
import com.eomcs.pms.dao.TaskDao;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.domain.Project;

public class ProjectService {

  SqlSession sqlSession;

  ProjectDao projectDao;
  TaskDao taskDao;

  public ProjectService(SqlSession sqlSession, ProjectDao projectDao, TaskDao taskDao) {
    this.sqlSession = sqlSession;
    this.projectDao = projectDao;
    this.taskDao = taskDao;
  }

  // 등록 업무
  public int add(Project project) throws Exception {
    try {
      // 1) 프로젝트 정보를 입력한다.
      int count = projectDao.insert(project);

      // 2) 멤버를 입력한다.
      projectDao.insertMembers(project.getNo(), project.getMembers());

      sqlSession.commit();
      return count;

    } catch (Exception e) {
      sqlSession.rollback();
      throw e;
    }
  }

  // 조회 업무
  public List<Project> list() throws Exception {
    return projectDao.findByKeyword(null, null);
  }

  // 상세 조회 업무
  public Project get(int no) throws Exception {
    return projectDao.findByNo(no);
  }

  // 변경 업무
  public int update(Project project) throws Exception {
    try {
      // 1) 프로젝트 정보를(만) 변경한다.
      int count = projectDao.update(project);

      // 2) 프로젝트의 기존 멤버를 모두 삭제한다.
      projectDao.deleteMembers(project.getNo());

      // 3) 프로젝트 멤버를 추가한다.
      projectDao.insertMembers(project.getNo(), project.getMembers());

      sqlSession.commit();
      return count;
    } catch (Exception e) {
      sqlSession.rollback();

      throw e;
    }
  }

  // 삭제 업무
  public int delete(int no) throws Exception {
    try {
      // 1) 프로젝트의 모든 작업 삭제
      taskDao.deleteByProjectNo(no);

      // 2) 프로젝트 멤버 삭제
      projectDao.deleteMembers(no);

      // 3) 프로젝트 삭제
      int count = projectDao.delete(no);
      sqlSession.commit();
      return count;

    } catch (Exception e) {
      sqlSession.rollback();
      throw e;
    }
  }

  // 찾기
  public List<Project> search(String title, String owner, String member) throws Exception {
    return projectDao.findByKeywords(title, owner, member);
  }

  // 위의 search와 같은 이름이지만 파라미터는 다르다! -> 오버로딩
  public List<Project> search(String item, String keyword) throws Exception {
    return projectDao.findByKeyword(item, keyword);
  }

  public int deleteMembers(int projectNo) throws Exception {
    int count = projectDao.deleteMembers(projectNo);
    sqlSession.commit();
    return count;
  }

  public int updateMembers(int projectNo, List<Member> members) throws Exception {
    try {
      projectDao.deleteMembers(projectNo);
      int count =projectDao.insertMembers(projectNo, members);
      sqlSession.commit();
      return count;

    } catch (Exception e) {
      sqlSession.rollback();
      throw e;
    }
  }
}

















