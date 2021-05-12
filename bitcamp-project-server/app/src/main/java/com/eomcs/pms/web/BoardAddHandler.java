package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Board;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.service.BoardService;

@SuppressWarnings("serial")
@WebServlet("/board/add")
public class BoardAddHandler extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
  // GET 요청에 대해서는 이 서블릿은 동작하지 않는다. post 요청에 대해서만 동작하게 된다.
      throws ServletException, IOException {

    BoardService boardService = (BoardService) request.getServletContext().getAttribute("boardService");

    Board b = new Board();

    // 클라이언트가 POST 요청으로 보낸 데이터가 UTF-8임을 알려준다.
    request.setCharacterEncoding("UTF-8");

    b.setTitle(request.getParameter("title"));
    b.setContent(request.getParameter("content"));

    // 작성자는 로그인 사용자이다.
    HttpServletRequest httpRequest = request;
    Member loginUser = (Member) httpRequest.getSession().getAttribute("loginUser"); // 클라이언트가 요청할 때 세션이 만들어진다.
    b.setWriter(loginUser);

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>게시글 등록</title>");

    try {
      boardService.add(b);

      out.println("<meta http-equiv='Refresh' content='1;url=list'>");  
      // '1;list': 1초 후에 다시 list를 요청하세요~ => 무조건 head 안에 들어있어야 하는 내용!
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>게시글 등록</h1>");
      out.println("<p>게시글을 등록했습니다.</p>");

    } catch (Exception e) {
      // 상세 오류 내용을 StringWriter로 출력한다.
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);

      out.println("</head>"); // add하다가 에러가 발생하면 바로 head 멈춤
      out.println("<body>");
      out.println("<h1>게시글 등록오류</h1>");
      out.printf("<pre>%s</pre>\n", strWriter.toString());
      out.println("<p><a href='list'>목록</a></p>"); // 목록으로 돌아갈 수 있는 링크
    }

    out.println("</body>");
    out.println("</html>");
  }
}






