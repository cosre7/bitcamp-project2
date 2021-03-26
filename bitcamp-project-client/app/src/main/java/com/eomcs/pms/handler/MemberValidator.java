package com.eomcs.pms.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.eomcs.util.Prompt;

public class MemberValidator {

  public String inputMember(String promptTitle) throws Exception {

    try (Connection con = DriverManager.getConnection( //
        "jdbc:mysql://localhost:3306/studydb?user=study&password=1111");
        PreparedStatement stmt = con.prepareStatement( //
            "select count(*) from pms_member where name=?")) {
      // name을 가진 데이터가 pms_member에 몇개가 있냐?
      // 없으면 0 -> 무조건 결과는 있다!

      while (true) {
        String name = Prompt.inputString(promptTitle);
        if (name.length() == 0) {
          return null;
        } 
        stmt.setString(1, name);

        try (ResultSet rs = stmt.executeQuery()) {
          rs.next(); // 서버의 결과를 가져온다.
          if (rs.getInt(1) > 0) {
            // 1 = count(*) // 어차피 컬럼은 한개!
            return name;
          } 
          System.out.println("등록되지 않은 회원입니다.");
        }
      }
    }
  }

  public String inputMembers(String promptTitle) throws Exception {
    String members = "";
    while (true) {
      String name = inputMember(promptTitle);
      if (name == null) {
        return members;
      } else {
        if (!members.isEmpty()) {
          members += ",";
        }
        members += name;
      }
    }
  }

}






