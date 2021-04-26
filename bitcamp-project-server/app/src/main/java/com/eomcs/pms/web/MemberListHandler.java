package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.service.MemberService;

@WebServlet("/member/list")
public class MemberListHandler implements Servlet {

  @Override
  public void service(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {

    MemberService memberService = (MemberService) request.getServletContext().getAttribute("memberService");

    response.setContentType("text/plain;charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("[회원 목록]");

    try {
      List<Member> list = memberService.list();

      for (Member m : list) {
        out.printf("%d, %s, %s, %s, %s\n", 
            m.getNo(), 
            m.getName(), 
            m.getEmail(),
            m.getPhoto(),
            m.getTel());
      }
    } catch (Exception e) {
      // 상세 오류 내용을 StringWriter로 출력한다.
      StringWriter strWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(strWriter);
      e.printStackTrace(printWriter);

      // StringWriter에 들어 있는 출력 내용을 꺼내 클라이언트로 보낸다.
      out.println(strWriter.toString());
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    // TODO Auto-generated method stub

  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public ServletConfig getServletConfig() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getServletInfo() {
    return null;
  }


}






