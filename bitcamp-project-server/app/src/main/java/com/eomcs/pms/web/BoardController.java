package com.eomcs.pms.web;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.eomcs.pms.domain.Board;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.service.BoardService;

@Controller
@RequestMapping("/board/") // 위아래 url이 합해짐 -> 뒤의 / 를 안붙여도 상관없다.
public class BoardController {

  BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @RequestMapping(path="add", method = RequestMethod.GET) 
  public String form() throws Exception {
    return "/jsp/board/form.jsp";
  }

  @RequestMapping(path="add", method = RequestMethod.POST) 
  public String add(Board b, HttpSession session) throws Exception { 
    // 파라미터 타입과 setter, getter(프로퍼티명)가 같으면 Board만 넣어주면 된다.
    Member loginUser = (Member) session.getAttribute("loginUser");
    b.setWriter(loginUser);

    boardService.add(b);
    return "redirect:list";
  }

  @RequestMapping(path = "delete", method = RequestMethod.GET) // path와 value 는 같다
  public String delete(int no, HttpSession session) throws Exception {

    Board oldBoard = boardService.get(no);
    if (oldBoard == null) {
      throw new Exception("해당 번호의 게시글이 없습니다.");
    }

    Member loginUser = (Member) session.getAttribute("loginUser");
    if (oldBoard.getWriter().getNo() != loginUser.getNo()) {
      throw new Exception("삭제 권한이 없습니다!");
    }

    boardService.delete(no);

    return "redirect:list";
  }

  @RequestMapping(path = "detail", method = RequestMethod.GET)
  public String detail(int no, Model model) throws Exception {
    model.addAttribute("board", boardService.get(no));
    return "/jsp/board/detail.jsp";
  }

  @RequestMapping(value = "list", method = RequestMethod.GET)
  public String list(String keyword, HttpServletRequest request) throws Exception {
    List<Board> boards = null;
    if (keyword != null && keyword.length() > 0) {
      boards = boardService.search(keyword);
    } else {
      boards = boardService.list();
    }

    request.setAttribute("list", boards);

    return "/jsp/board/list.jsp";
  }

  @RequestMapping(value = "update", method = RequestMethod.POST)
  public String update(Board board, HttpSession session) throws Exception {

    Board oldBoard = boardService.get(board.getNo());
    if (oldBoard == null) {
      throw new Exception("해당 번호의 게시글이 없습니다.");
    } 

    Member loginUser = (Member) session.getAttribute("loginUser");
    if (oldBoard.getWriter().getNo() != loginUser.getNo()) {
      throw new Exception("변경 권한이 없습니다!");
    }

    boardService.update(board);

    return "redirect:list";
  }
}






