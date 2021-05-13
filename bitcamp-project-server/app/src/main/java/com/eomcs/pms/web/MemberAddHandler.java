package com.eomcs.pms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.service.MemberService;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;

@SuppressWarnings("serial")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // Multipart를 원하는 경우 손들기!
@WebServlet("/member/add")
public class MemberAddHandler extends HttpServlet {

  private String uploadDir;

  @Override
  public void init() throws ServletException {
    this.uploadDir = this.getServletContext().getRealPath("/upload");
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
    out.println("<title>회원 등록</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>회원 등록</h1>");

    try {
      Member m = new Member();
      m.setName(request.getParameter("name"));
      m.setEmail(request.getParameter("email"));
      m.setPassword(request.getParameter("password"));
      m.setTel(request.getParameter("tel"));

      Part photoPart = request.getPart("photo"); // 파일의 경우 getPart를 해야 한다.
      if (photoPart.getSize() > 0) {
        // 파일을 선택해서 업로드 했다면,
        String filename = UUID.randomUUID().toString();
        photoPart.write(this.uploadDir + "/" + filename);
        m.setPhoto(filename);
        // 사진파일의 경우 확장자명을 적어주지 않아도 된다.

        // 썸네일 이미지 생성
        Thumbnails.of(this.uploadDir + "/" + filename)
        .size(30, 30)
        .outputFormat("jpg")
        .crop(Positions.CENTER)
        .toFiles(new Rename() {
          @Override
          public String apply(String name, ThumbnailParameter param) {
            return name + "_30x30";
          }
        });

        Thumbnails.of(this.uploadDir + "/" + filename)
        .size(80, 80)
        .outputFormat("jpg")
        .crop(Positions.CENTER)
        .toFiles(new Rename() {
          @Override
          public String apply(String name, ThumbnailParameter param) {
            return name + "_80x80";
          }
        });
      }
      memberService.add(m);

      out.println("<p>회원을 등록했습니다.</p>");

      // 응답헤더에 리프레시 정보를 설정한다.
      response.setHeader("Refresh", "1;url=list"); // 1초 후에 url=list로 가라!

      // 질문!
      //   클라이언트에게 응답할 때 헤더를 먼저 보내고 콘텐트를 나중에 보내는데 
      //   위의 코드를 보면 pringln()을 이용하여 콘텐트를 먼저 출력한 다음에
      //   응답 헤더를 설정하는데 이것이 가능한가요?
      // - println()을 실행할 때 출력 내용은 모두 버퍼로 보낸다.
      // - 즉 아직 클라이언트에게 응답한 상태가 아니기 때문에 응답 헤더를 설정할 수 있는 것이다.

      // - 출력 버퍼를 다 채워야 보내지는데 위의 내용으로는 보내지지 않기 때문
      // - 어차피 버퍼에 쌓이는 중이지 보내진 상태가 아니기 때문에 서버로 보내지지 않는다
      //   때문에 리프레시 정보를 후에 설정해도 상관이 없다.
      // - 버퍼에 쌓인 내용은 메서드 호출이 모두 끝난 후에 서버로 보내진다.
      // - 정보 설정 전에 이미 버퍼가 쌓여버리면 서버로 보내져버려서 리프레시가 되지 않는다.

    } catch (Exception e) {
      request.setAttribute("exception", e); 
      request.getRequestDispatcher("/error").forward(request, response);
      return;
    }

    out.println("</body>");
    out.println("</html>");
  }
}






