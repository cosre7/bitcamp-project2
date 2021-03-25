package com.eomcs.pms.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BoardListHandler implements Command {

  @Override
  public void service() throws Exception {
    System.out.println("[게시글 목록]");

    try (Connection con = DriverManager.getConnection( 
        "jdbc:mysql://localhost:3306/studydb?user=study&password=1111");
        PreparedStatement stmt = con.prepareStatement( 
            "select no,title,writer,cdt,vw_cnt from pms_board order by no desc");
        // select * 를 해버리면 content까지 읽어온다.
        // 쓰지도 않을 데이터..심지어 되게 큰 용량일것
        // 걍 받지도말자.
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        System.out.printf("%d, %s, %s, %s, %d\n", 
            rs.getInt("no"), 
            rs.getString("title"), 
            rs.getString("writer"), 
            rs.getDate("cdt"), // getString을 하게되면 시간까지!
            rs.getInt("vw_cnt"));
      }
    }
  }
}






