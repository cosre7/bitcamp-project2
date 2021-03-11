package com.eomcs.pms.handler;

import java.util.Iterator;
import com.eomcs.driver.Statement;
import com.eomcs.util.Prompt;

public class MemberDetailHandler implements Command {

  @Override
  public void service(Statement stmt) throws Exception {
    System.out.println("[회원 상세보기]");

    int no = Prompt.inputInt("번호? ");

    Iterator<String> results = stmt.executeQuery("member/select", Integer.toString(no));

    String[] fields  = results.next().split(",");

    System.out.printf("이름: %s\n", fields[1]);
    System.out.printf("이메일: %s\n", fields[2]);
    System.out.printf("사진: %s\n", fields[3]);
    System.out.printf("전화: %s\n", fields[4]);
    System.out.printf("가입일: %s\n", fields[5]);

  }
}






