package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Task;
import com.eomcs.pms.service.TaskService;

@SuppressWarnings("serial")
@WebServlet("/task/list")
public class TaskListHandler extends HttpServlet {

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    TaskService taskService = (TaskService) request.getServletContext().getAttribute("taskService");

    response.setContentType("text/plain;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("[작업 목록]");

    // 1) 사용자가 입력한 문자열을 프로젝트 번호로 바꾼다.
    int projectNo = 0;
    try {
      projectNo = Integer.parseInt(request.getParameter("projectNo"));
    }catch (Exception e) {
      out.println("프로젝트 번호를 입력하세요.");
      return;
    }

    // 2) 해당 프로젝트에 소속된 작업 목록을 가져온다.
    try {
      List<Task> tasks = null;
      if (projectNo == 0) {
        tasks = taskService.list();
      } else {
        tasks = taskService.listOfProject(projectNo);
      }

      if (tasks.size() == 0) {
        out.println("해당 번호의 프로젝트가 없거나 또는 등록된 작업이 없습니다.");
        return;
      }

      projectNo = 0;
      for (Task t : tasks) {
        if (projectNo != t.getProjectNo()) {
          out.printf("'%s' 작업 목록: \n", t.getProjectTitle());
          projectNo = t.getProjectNo();
        }
        out.printf("%d, %s, %s, %s, %s\n", 
            t.getNo(), 
            t.getContent(), 
            t.getDeadline(),
            t.getOwner().getName(),
            Task.getStatusLabel(t.getStatus()));
      }
    } catch (Exception e) {
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);
      out.println(strWriter.toString());
    }
  }
}
