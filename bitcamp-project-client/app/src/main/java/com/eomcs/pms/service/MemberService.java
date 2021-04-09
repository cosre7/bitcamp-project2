package com.eomcs.pms.service;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import com.eomcs.pms.dao.MemberDao;
import com.eomcs.pms.domain.Member;

public class MemberService {

  // DAO가 사용하는 SqlSession 객체를 주입 받아야 한다.
  SqlSession sqlSession;

  // 비즈니스 로직을 수행하는 동안 데이터 처리를 위해 사용할 DAO를 주입 받아야 한다.
  MemberDao memberDao;

  public MemberService(SqlSession sqlSession, MemberDao memberDao) {
    this.sqlSession = sqlSession;
    this.memberDao = memberDao;
  }

  // 등록 업무
  public int add(Member member) throws Exception {
    int count = memberDao.insert(member);
    sqlSession.commit();
    return count;
  }

  // 조회 업무
  public List<Member> list() throws Exception {
    return memberDao.findAll();
  }

  // 상세 조회 업무
  public Member get(int no) throws Exception {
    return memberDao.findByNo(no);
  }

  // 변경 업무
  public int update(Member member) throws Exception {
    int count = memberDao.update(member);
    sqlSession.commit();
    return count;
  }

  // 삭제 업무
  public int delete(int no) throws Exception {
    int count = memberDao.delete(no);
    sqlSession.commit();
    return count;
  }

  // 이름으로 찾기
  public Member search(String name) throws Exception {
    return memberDao.findByName(name);
  }
}

















