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
import com.eomcs.pms.domain.Board;
import com.eomcs.pms.service.BoardService;

@SuppressWarnings("serial")
@WebServlet("/board/list")
public class BoardListHandler extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // 클라이언트가 /board/list를 요청하면 톰캣 서버가 이 메서드를 호출한다.

    BoardService boardService = (BoardService) request.getServletContext().getAttribute("boardService");

    response.setContentType("text/html;charset=UTF-8"); // 브라우저야~ html 규칙에 따라서 만들어줘~
    // 서블릿을 통해 실행할 경우 charset이 utf-8로 설정해주기 때문에 html 규칙 안에 meta charset=utf-8 을
    // 따로 설정할 필요가 없다.
    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>게시글 목록</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>게시글 목록</h1>");

    out.println("<p><a href='form.html'>새 글</a></p>");

    try {
      List<Board> boards = boardService.list();

      out.println("<table border='1'>");
      out.println("<thead>");
      out.println("<tr>");
      out.println("<th>번호</th> <th>제목</th> <th>작성자</th> <th>등록일</th> <th>조회수</th>");
      out.println("</tr>");
      out.println("</thead>");
      out.println("<tbody>");
      // println을 쓰는 이유 -> 페이지 소스보기를 할 때 한 줄씩 띄워서 보기 편하게 하기 위함일 뿐
      // 엡 브라우저와는 관계가 없다.

      for (Board b : boards) {
        out.printf("<tr>"
            + " <td>%d</td>"
            + " <td><a href='detail?no=%1$d'>%s</a></td>" // board/detail을 보고 싶을 때 -> list와 앞까지는 같은 경루 => 상대경로 detail만 적는다.
            + " <td>%s</td>"
            + " <td>%s</td>"
            + " <td>%d</td> </tr>\n",  
            // \n을 해주지 않으면 브라우저에서 소스를 볼 때 한 줄로 쭈우우욱 나온다.
            b.getNo(), 
            b.getTitle(), 
            b.getWriter().getName(),
            b.getRegisteredDate(),
            b.getViewCount());
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






