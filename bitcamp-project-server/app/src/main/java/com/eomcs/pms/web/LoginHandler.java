package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.service.MemberService;

@SuppressWarnings("serial")
@WebServlet("/login")
public class LoginHandler extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // 클라이언트가 보낸 쿠키 값 중에서 email 이름의 값을 꺼낸다.
    String email = "";

    Cookie[] cookies = request.getCookies();
    if (cookies != null) { 
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("email")) { // 쿠키에 포함된 값 중에서 이메일이라는 이름의 값이 있다면 꺼낸다.
          email = cookie.getValue();
          break;
        }
      }
    }

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>로그인</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>로그인</h1>");

    out.println("<form method='post'>");
    out.println("<table border='1'>");
    out.println("<tbody>");
    out.printf("<tr><th>이메일</th>"
        + "<td><input name='email' type='email'></td></tr>\n", email);
    out.println("<tr><th>암호</th><td><input name='password' type='password'></td></tr>");
    out.println("</tbody>");

    out.println("<tfoot>");
    out.println("<tr><td colspan='2'><input type='checkbox' name='saveEmail'>이메일 저장</td></tr>");
    // checkbox : 체크를 하면 on 이라는 값이 넘어가고 체크하지 않으면 아무런 값도 넘어가지 않는다.
    out.println("<tr><td colspan='2'><button>로그인</button></td></tr>");
    out.println("</tfoot>");
    out.println("</table>");
    out.println("</form>");

    out.println("</body>");
    out.println("</html>");

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    MemberService memberService = (MemberService) request.getServletContext().getAttribute("memberService");

    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<title>로그인</title>");

    String email = request.getParameter("email");
    String password = request.getParameter("password");

    // 클라이언트에게 쿠키를 보내기 
    if (request.getParameter("saveEmail") != null) { // null 이 아니면 -> 값이 있으면
      Cookie cookie = new Cookie("email", email);
      cookie.setMaxAge(60 * 60 * 24 * 5); // 유효기간을 설정하지 않으면 웹브라우저가 실행되는 동안만 유지하라는 의미가 된다.
      response.addCookie(cookie);

    } else {
      // 기존에 있는 쿠키도 제거한다.
      Cookie cookie = new Cookie("email", "");
      cookie.setMaxAge(0); // 유효기간(초)을 0으로 하면 웹 브라우저는 email 이름으로 저장된 쿠키를 제거한다.
      response.addCookie(cookie);
      // 쿠키를 제거하든 아니든 일단 보내야 한다.
    }

    try {

      Member member = memberService.get(email, password);
      if (member == null) {
        // 로그인 실패한다면 세션 객체의 모든 내용을 삭제한다.
        request.getSession().invalidate(); 
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>로그인 결과</h1>");
        out.println("<p>사용자 정보가 맞지 않습니다.</p>");

        response.sendRedirect("login"); // 로그인으로 가랏 -> 상대경로

        // 주석 가져오기

        // 로그인을 요청한 후 응답 결과를 보면 위와 같다.
        // 여기서 주목해야 할 사항은 응답에 println()으로 출력한 결과가 없다는 것이다.
        // 왜?
        // - sendRedirect()를 호출하면 버퍼에 들어 있는 출력 내용을 버린다.
        // - 응답할 때 콘텐트를 보내지 않기 때문이다.
        // - 따라서 리다이렉트를 할 생각이라면 콘텐트를 출력하지 말라!! -> 부질없는 짓이다. 어차피 무시됨
        // - 302 응답헤더만 보낼 뿐
      } else {
        // 로그인 성공한다면, 로그인 사용자 정보를 세션 객체에 보관한다.
        request.getSession().setAttribute("loginUser", member);      

        out.println("</head>");
        out.println("<body>");
        out.println("<h1>로그인 결과</h1>");
        out.printf("<p>%s 님 환영합니다.</p>\n", member.getName());

        response.sendRedirect("userInfo"); // userInfo로 가랏 -> 상대경로
      }


    } catch (Exception e) {
      request.setAttribute("exception", e); 
      request.getRequestDispatcher("/error").forward(request, response);
      return;
    }

    out.println("</body>");
    out.println("</html>");
  }
}






