package com.eomcs.pms.web;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

  @RequestMapping(path="form", method = RequestMethod.GET) 
  /// void가 되면 board/add.jsp를 찾게 될 것이다. -> return 필요
  /// 또는 path를 form으로 바꾼다.
  public void form() throws Exception {
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
  public void detail(int no, Model model) throws Exception {
    model.addAttribute("board", boardService.get(no));
  }

  @RequestMapping(path = "{no}", method = RequestMethod.GET)
  public String detail2(@PathVariable int no, Model model) throws Exception {
    model.addAttribute("board", boardService.get(no));
    return "board/detail";
  }

  @RequestMapping(value = "list", method = RequestMethod.GET) /// 주소를 리턴하지 않으면 board/list를 jsp의 주소로도 사용한다.
  public void list(String keyword, 
      @RequestParam(defaultValue = "1") int pageNo,  
      @RequestParam(defaultValue = "3") int pageSize, 
      Model model) throws Exception {

    int count = boardService.count(keyword);

    if (pageSize < 3 || pageSize > 10) {
      pageSize = 3;
    }

    int totalPage = count / pageSize + (count % pageSize) > 0 ? 1 : 0;

    if (pageNo < 1 || pageNo > totalPage) { // 최대 페이지 수가 5인데 6을 달라고 한다? 1페이지 준다.
      pageNo = 1;
    }

    List<Board> boards = null;
    if (keyword != null && keyword.length() > 0) {
      boards = boardService.search(keyword, pageNo, pageSize);
    } else {
      boards = boardService.list(pageNo, pageSize);
    }

    model.addAttribute("list", boards); /// list.jsp 사라짐
    model.addAttribute("totalPage", totalPage);
    model.addAttribute("pageNo", pageNo);
    model.addAttribute("pageSize", pageSize);

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






