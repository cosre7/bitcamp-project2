package com.eomcs.pms.handler;

import com.eomcs.pms.service.BoardService;
import com.eomcs.stereotype.Component;
import com.eomcs.util.Prompt;

@Component(value="/board/delete")
public class BoardDeleteHandler implements Command {

  //핸들러가 사용할 Service 객체
  BoardService boardService;

  // Service 객체는 이 클래스가 작업하는데 필수 객체이기 때문에
  // 생성자를 통해 반드시 주입 받도록 한다.
  public BoardDeleteHandler(BoardService boardService) {
    this.boardService = boardService;
  }

  @Override
  public void service() throws Exception {
    System.out.println("[게시글 삭제]");

    int no = Prompt.inputInt("번호? ");

    String input = Prompt.inputString("정말 삭제하시겠습니까?(y/N) ");
    if (!input.equalsIgnoreCase("Y")) {
      System.out.println("게시글 삭제를 취소하였습니다.");
      return;
    }

    if (boardService.delete(no) == 0) {
      System.out.println("해당 번호의 게시글이 없습니다.");
    } else {
      System.out.println("게시글을 삭제하였습니다.");
    }
  }
}






