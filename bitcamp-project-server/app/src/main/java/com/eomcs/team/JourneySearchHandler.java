package com.eomcs.team;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Journey;
import com.eomcs.pms.service.JourneyService;
import com.eomcs.pms.web.SearchException;

@SuppressWarnings("serial")
@WebServlet("/journey/search")
public class JourneySearchHandler extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String dept = request.getParameter("dept");
    String arr = request.getParameter("arr");

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter(); 

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>여정 검색</title>");
    out.println("<style>");
    out.println("body { background-image: url('background_img.jpg') }");
    out.println("h1 { text-align: center; }");
    out.println("</style>");
    out.println("</head>");
    out.println("<body>");
    out.println("<div style='height:300px; width:500px; background-color:#e4f2ef'>");
    out.println("<h1>함께 할 드라이버를 찾아볼까요!</h1>");
    out.print("<form>");
    out.print("<input type='text'>");
    out.print("<input type='text'>");
    out.print("</form>");
    out.print("<form>");
    out.print("<input type='text'>");
    out.print("<input type='text'>");
    out.print("</form>");
    out.println("</div>");

    out.println("<table border='1'>");
    out.println("<thead>");
    out.println("<tr>");
    out.println("<th>드라이버</th> <th>출발지</th> <th>경유지</th> <th>도착지</th> <th>시간</th> <th>일자</th> <th>채팅</th> <th>신청</th>");
    out.println("</tr>");
    out.println("</thead>");
    out.println("<tbody>");
    out.println("</tbody>");
    out.println("</table>");
    try {
      if (dept == null || dept.length() == 0) {
        throw new SearchException("검색어를 입력하세요.");
      }

      JourneyService journeyService = (JourneyService) request.getServletContext().getAttribute("journeyService");
      List<Journey> list = journeyService.search(dept);
      if (list.size() == 0) {
        throw new SearchException("검색어에 해당하는 여정이 없습니다.");
      }



    } catch (SearchException e) {
      out.printf("<p>%s</p>\n", e.getMessage());

    } catch (Exception e) {
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);

      out.printf("<pre>%s</pre>\n", strWriter.toString());
    }

    out.println("</body>");
    out.println("</html>");
  }
}
