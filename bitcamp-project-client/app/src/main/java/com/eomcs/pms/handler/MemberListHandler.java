package com.eomcs.pms.handler;

import java.util.List;
import com.eomcs.pms.dao.MemberDao;
import com.eomcs.pms.domain.Member;

public class MemberListHandler implements Command {

  MemberDao memberDao;

  public MemberListHandler(MemberDao memberDao) {
    this.memberDao = memberDao;
  }

  @Override
  public void service() throws Exception {
    System.out.println("[회원 목록]");

    List<Member> list = memberDao.findAll();

    for (Member m : list) {
      System.out.printf("%d, %s, %s, %s, %s\n", 
          m.getNo(), 
          m.getName(), 
          m.getEmail(),
          m.getPhoto(),
          m.getTel());
    }
  }
}






