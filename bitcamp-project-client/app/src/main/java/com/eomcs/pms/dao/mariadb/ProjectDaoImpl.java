package com.eomcs.pms.dao.mariadb;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import com.eomcs.pms.dao.ProjectDao;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.domain.Project;

public class ProjectDaoImpl implements ProjectDao {

  SqlSession sqlSession;

  public ProjectDaoImpl(SqlSession sqlSession) throws Exception {
    this.sqlSession = sqlSession;
  }

  @Override
  public int insert(Project project) throws Exception {
    return sqlSession.insert("ProjectMapper.insert", project);
  }

  @Override
  public List<Project> findByKeyword(Map<String,Object> params) throws Exception {
    return sqlSession.selectList("ProjectMapper.findByKeyword", params);
  }

  @Override
  public List<Project> findByKeywords(Map<String,Object> params) throws Exception {
    return sqlSession.selectList("ProjectMapper.findByKeywords", params);
  }

  @Override
  public Project findByNo(int no) throws Exception {
    return sqlSession.selectOne("ProjectMapper.findByNo", no);
  }

  @Override
  public int update(Project project) throws Exception {
    return sqlSession.update("ProjectMapper.update", project);
  }

  @Override
  public int delete(int no) throws Exception {
    return sqlSession.delete("ProjectMapper.delete", no);
  }

  @Override
  public int insertMember(Map<String,Object> params) throws Exception {
    return sqlSession.insert("ProjectMapper.insertMember", params);
  }

  @Override
  public int insertMembers(Map<String,Object> params) throws Exception {
    return sqlSession.insert("ProjectMapper.insertMembers", params);
  }

  @Override
  public List<Member> findAllMembers(int projectNo) throws Exception {
    return sqlSession.selectList("ProjectMapper.findAllMembers", projectNo);
  }

  @Override
  public int deleteMembers(int projectNo) throws Exception {
    return sqlSession.delete("ProjectMapper.deleteMembers", projectNo);
  }
}












