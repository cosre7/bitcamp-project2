package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Project;
import com.eomcs.pms.domain.Task;
import com.eomcs.pms.service.TaskService;

@SuppressWarnings("serial")
@WebServlet("/task/add")
public class TaskAddHandler extends HttpServlet {

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    TaskService taskService = (TaskService) request.getServletContext().getAttribute("taskService");

    response.setContentType("text/plain;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("[작업 등록]");

    // 1) 현재 등록된 프로젝트 목록을 가져온다.
    List<Project> projects = projectService.list();


    // 2) 프로젝트 목록을 출력한다.
    out.println("프로젝트들:");
    if (projects.size() == 0) {
      out.println("현재 등록된 프로젝트가 없습니다!");
      return;
    }
    for (Project p : projects) {
      out.printf("  %d, %s\n", p.getNo(), p.getTitle());
    }

    // 3) 작업을 등록할 프로젝트를 선택한다.
    int selectedProjectNo = 0;
    loop: while (true) {
      String input = prompt.inputString("프로젝트 번호?(취소: 빈 문자열) ");
      if (input.length() == 0) {
        out.println("작업 등록을 취소합니다.");
        return;
      }
      try {
        selectedProjectNo = Integer.parseInt(input);
      } catch (Exception e) {
        out.println("숫자를 입력하세요!");
        continue;
      }
      for (Project p : projects) {
        if (p.getNo() == selectedProjectNo) {
          break loop;
        }
      }
      out.println("유효하지 않은 프로젝트 번호 입니다.");
    }

    // 4) 작업 정보를 입력 받는다.
    Task t = new Task();
    t.setProjectNo(selectedProjectNo);
    t.setContent(prompt.inputString("내용? "));
    t.setDeadline(prompt.inputDate("마감일? "));
    t.setStatus(prompt.inputInt("상태?\n0: 신규\n1: 진행중\n2: 완료\n> "));

    t.setOwner(memberValidator.inputMember("담당자?(취소: 빈 문자열) ", request, response));
    if (t.getOwner() == null) {
      out.println("작업 등록을 취소하였습니다.");
      return;
    }

    taskService.add(t);

    out.println("작업을 등록했습니다.");
  }
}
