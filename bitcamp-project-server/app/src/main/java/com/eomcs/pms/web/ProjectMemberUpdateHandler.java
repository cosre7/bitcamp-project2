package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.domain.Project;
import com.eomcs.pms.service.ProjectService;

@SuppressWarnings("serial")
@WebServlet("/project/memberUpdate")
public class ProjectMemberUpdateHandler extends HttpServlet {

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    ProjectService projectService = (ProjectService) request.getServletContext().getAttribute("projectService");

    response.setContentType("text/plain;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("[프로젝트 멤버 변경]");

    try {
      int no = Integer.parseInt(request.getParameter("no"));

      Project project = projectService.get(no);

      if (project == null) {
        out.println("해당 번호의 프로젝트가 없습니다.");
        return;
      }

      Member loginUser = (Member) request.getSession().getAttribute("loginUser");
      if (project.getOwner().getNo() != loginUser.getNo()) {
        out.println("변경 권한이 없습니다!");
        return;
      }

      out.printf("프로젝트 명: %s\n", project.getTitle());
      out.println("멤버:");
      for (Member m : project.getMembers()) {
        out.printf("  %s(%d)\n", m.getName(), m.getNo());
      }
      out.println("---------------------------");

      // ...&member=1&member=2&member=23
      String[] values = request.getParameterValues("member"); // 여러개의 같은 이름의 파라미터
      ArrayList<Member> memberList = new ArrayList<>();
      for (String value : values) {
        Member member = new Member();
        member.setNo(Integer.parseInt(value));
        memberList.add(member);
      }

      // 프로젝트의 멤버를 변경한다.
      projectService.updateMembers(no, memberList);

      out.println("프로젝트 멤버를 변경하였습니다.");

    } catch (Exception e) {
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);
      out.println(strWriter.toString());
    }
  }
}






