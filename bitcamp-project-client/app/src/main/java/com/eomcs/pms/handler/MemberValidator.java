package com.eomcs.pms.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.eomcs.pms.domain.Member;

public class MemberValidator {

  public String inputMember(String promptTitle) throws Exception {

    Member member = new Member();

    try (Connection con = DriverManager.getConnection( //
        "jdbc:mysql://localhost:3306/studydb?user=study&password=1111");
        PreparedStatement stmt = con.prepareStatement( //
            "select * from pms_member where name=?")) {

      stmt.setString(1, member.getName());

      try (ResultSet rs = stmt.executeQuery()) {
        if (!rs.next())
          return null;
        return member.getName();
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






