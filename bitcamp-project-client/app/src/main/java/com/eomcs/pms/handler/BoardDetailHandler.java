package com.eomcs.pms.handler;

import java.text.SimpleDateFormat;
import com.eomcs.pms.dao.BoardDao;
import com.eomcs.pms.domain.Board;
import com.eomcs.util.Prompt;

public class BoardDetailHandler implements Command {

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
  // 실무에서는 밑의 위치로
  // 현재는 멀티스레드 상황이 아니기때문에 상관없다!

  @Override
  public void service() throws Exception {
    System.out.println("[게시글 상세보기]");

    int no = Prompt.inputInt("번호? ");

    Board b = BoardDao.findByNo(no);

    if (b == null) {
      System.out.println("해당 번호의 게시글이 없습니다.");
      return;
    }

    //    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 여러 스레드가 동시에 진입하는 경우에 대비 -> service를 호출할 때 마다 새로운 인스턴스가 생성되어야 한다.
    // 이 객체는 공유되어서는 안된다!! - Thread safe하지않다!
    // 그래서 실무에서는 현재의 위치로! -> thread safe를 위해
    // String 을 계속 new String하는거랑 같다고 생각하자~~

    System.out.printf("제목: %s\n", b.getTitle());
    System.out.printf("내용: %s\n", b.getContent());
    System.out.printf("작성자: %s\n", b.getWriter().getName());
    System.out.printf("등록일: %s\n", formatter.format(b.getRegisteredDate())); 
    System.out.printf("조회수: %s\n", b.getViewCount());
    System.out.printf("좋아요: %s\n", b.getLike());
  }
}






