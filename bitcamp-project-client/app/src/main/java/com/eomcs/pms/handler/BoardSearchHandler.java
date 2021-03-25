package com.eomcs.pms.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.eomcs.util.Prompt;

public class BoardSearchHandler implements Command {

  @Override
  public void service() throws Exception {
    String keyword = Prompt.inputString("검색어? ");

    if (keyword.length() == 0) {
      System.out.println("검색어를 입력하세요.");
      return;
    }

    try (Connection con = DriverManager.getConnection( 
        "jdbc:mysql://localhost:3306/studydb?user=study&password=1111");
        PreparedStatement stmt = con.prepareStatement( 
            "select no,title,writer,cdt,vw_cnt"
                + " from pms_board"
                + " where title like concat('%',?,'%')"
                + " or content like concat('%',?,'%')" // 공백 중요!!!
                + " or writer like concat('%',?,'%')" // ?와 같으냐? : like ?
                // like 'aaa%' : aaa로 시작하는 문자열을 포함
                // like '%aaa' : aaa로 끝나는 문자열을 포함
                // like '%aaa%' : aaa가 포함되는 문자열
                + " order by no desc")) {

      stmt.setString(1, keyword);
      stmt.setString(2, keyword);
      stmt.setString(3, keyword);

      try (ResultSet rs = stmt.executeQuery()) {
        if (!rs.next()) { // 가져온 결과가 없을 때
          System.out.println("검색어에 해당하는 게시글이 없습니다.");
          return;
        }

        do {
          System.out.printf("%d, %s, %s, %s, %d\n", 
              rs.getInt("no"), 
              rs.getString("title"), 
              rs.getString("writer"), 
              rs.getDate("cdt"), 
              rs.getInt("vw_cnt"));
        } while (rs.next()); // 바로 결과 검사를 해버리면 처음 가지고 온 값은 버려진다 -> do while 사용
      }
    }
  }
}






