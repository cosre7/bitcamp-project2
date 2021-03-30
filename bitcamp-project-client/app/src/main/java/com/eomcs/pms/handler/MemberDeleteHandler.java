package com.eomcs.pms.handler;

import com.eomcs.pms.dao.MemberDao;
import com.eomcs.util.Prompt;

public class MemberDeleteHandler implements Command {

  MemberDao memberDao;

  public MemberDeleteHandler(MemberDao memberDao) {
    this.memberDao = memberDao;
  }

  @Override
  public void service() throws Exception {
    System.out.println("[회원 삭제]");

    int no = Prompt.inputInt("번호? ");

    String input = Prompt.inputString("정말 삭제하시겠습니까?(y/N) ");
    if (!input.equalsIgnoreCase("Y")) {
      System.out.println("회원 삭제를 취소하였습니다.");
      return;
    }

    if (memberDao.delete(no) == 0) {
      System.out.println("해당 번호의 회원이 없습니다.");
    } else {
      System.out.println("회원을 삭제하였습니다.");
    }
  }
}






