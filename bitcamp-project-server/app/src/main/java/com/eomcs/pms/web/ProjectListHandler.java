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
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.domain.Project;
import com.eomcs.pms.service.ProjectService;

@SuppressWarnings("serial")
@WebServlet("/project/list")
public class ProjectListHandler extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    ProjectService projectService = (ProjectService) request.getServletContext().getAttribute("projectService");

    response.setContentType("text/html;charset=UTF-8"); 
    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>프로젝트</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>프로젝트</h1>");

    out.println("<p><a href='add'>새 프로젝트</a></p>"); // => /project/add의 doGet 실행

    try {
      List<Project> projects = projectService.list();

      out.println("<table border='1'>");
      out.println("<thead>");
      out.println("<tr>");
      out.println("<th>번호</th> <th>프로젝트명</th> <th>기간</th> <th>팀장</th> <th>팀원</th>");
      out.println("</tr>");
      out.println("</thead>");
      out.println("<tbody>");

      for (Project p : projects) {

        // 1) 프로젝트의 팀원 목록 가져오기
        StringBuilder strBuilder = new StringBuilder();
        List<Member> members = p.getMembers();
        for (Member m : members) {
          if (strBuilder.length() > 0) {
            strBuilder.append(",");
          }
          strBuilder.append(m.getName());
        }

        // 2) 프로젝트 정보를 출력
        out.printf("<tr>"
            + " <td>%d</td>"
            + " <td><a href='detail?no=%1$d'>%s</a></td>" // board/detail을 보고 싶을 때 -> list와 앞까지는 같은 경루 => 상대경로 detail만 적는다.
            + " <td>%s ~ %s</td>"
            + " <td>%s</td>"
            + " <td>%s</td> </tr>\n",  
            p.getNo(), 
            p.getTitle(), 
            p.getStartDate(),
            p.getEndDate(),
            p.getOwner().getName(),
            strBuilder.toString());
      }
      out.println("</tbody>");
      out.println("</table>");

      out.println("<form action='search' method='get'>");
      out.println("<input type='text' name='keyword'>");
      out.println("<button type='submit'>검색</button>"); //type='submit' 생략하면 기본이 submit
      out.println("</form>");

    } catch (Exception e) {
      // 상세 오류 내용을 StringWriter로 출력한다.
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);

      out.printf("<pre>%s</pre>\n", strWriter.toString());
    }

    out.println("</body>");
    out.println("</html>");
  }
}








