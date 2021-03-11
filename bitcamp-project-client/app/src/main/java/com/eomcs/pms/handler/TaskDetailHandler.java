package com.eomcs.pms.handler;

import java.util.Iterator;
import com.eomcs.driver.Statement;
import com.eomcs.pms.domain.Task;
import com.eomcs.util.Prompt;

public class TaskDetailHandler implements Command {

  @Override
  public void service(Statement stmt) throws Exception {
    System.out.println("[작업 상세보기]");

    int no = Prompt.inputInt("번호? ");

    Iterator<String> results = stmt.executeQuery("task/select", Integer.toString(no));

    String[] fields = results.next().split(",");
    System.out.printf("내용: %s\n", fields[1]);
    System.out.printf("마감일: %s\n", fields[2]);
    System.out.printf("상태: %s\n", Task.getStatusLabel(Integer.parseInt(fields[3])));
    System.out.printf("담당자: %s\n", fields[4]);

  }
}
