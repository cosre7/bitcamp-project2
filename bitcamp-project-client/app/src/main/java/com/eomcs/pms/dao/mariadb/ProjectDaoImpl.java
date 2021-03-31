package com.eomcs.pms.dao.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.eomcs.pms.dao.ProjectDao;
import com.eomcs.pms.domain.Member;
import com.eomcs.pms.domain.Project;

public class ProjectDaoImpl implements ProjectDao {

  Connection con;

  //Connection 객체를 자체적으로 생성하지 않고 외부에서 주입받는다.
  // - Connection 객체를 여러 DAO가 공유할 수 있다.
  // - 교체하기도 쉽다.
  public ProjectDaoImpl(Connection con) throws Exception {
    this.con = con;
  }

  @Override
  public int insert(Project project) throws Exception {

    try (PreparedStatement stmt = con.prepareStatement(
        "insert into pms_project(title,content,sdt,edt,owner) values(?,?,?,?,?)",
        Statement.RETURN_GENERATED_KEYS)) {
      //Statement.RETURN_GENERATED_KEYS: 자동증가된 키값을 받겠다!
      con.setAutoCommit(false);

      // 1) 프로젝트를 추가한다.
      stmt.setString(1, project.getTitle());
      stmt.setString(2, project.getContent());
      stmt.setDate(3, project.getStartDate());
      stmt.setDate(4, project.getEndDate());
      stmt.setInt(5, project.getOwner().getNo());
      int count = stmt.executeUpdate();

      // 프로젝트 데이터의 PK 값 알아내기
      try (ResultSet keyRs = stmt.getGeneratedKeys()) {
        keyRs.next();
        project.setNo(keyRs.getInt(1));
      }

      // 2) 프로젝트에 팀원들을 추가한다.
      for (Member member : project.getMembers()) {
        insertMember(project.getNo(), member.getNo());
      }

      con.commit();

      return count;
    } catch (Exception e) {
      con.rollback();

      // 이 catch 블록의 목적은 예외를 처리 하는 것이 아니라,
      // rollback을 실행하는 것이다.
      // 따라서 예외가 발생한 사실은 이전처럼 호출자에게 그대로 보고해야 한다.
      throw e; // 호출자가 알 수 있도록 예외 던지기

    } finally { // 예외가 발생하던 하지않던 무조건 수행 하는 블럭!
      con.setAutoCommit(true);
    }
  }

  @Override
  public List<Project> findAll() throws Exception {
    ArrayList<Project> list = new ArrayList<>();

    try (PreparedStatement stmt = con.prepareStatement(
        "select" 
            + "    p.no,"
            + "    p.title,"
            + "    p.sdt,"
            + "    p.edt,"
            + "    m.no as owner_no,"
            + "    m.name as owner_name"
            + "  from pms_project p"
            + "    inner join pms_member m on p.owner=m.no"
            + "  order by title asc");
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Project project = new Project();
        project.setNo(rs.getInt("no"));
        project.setTitle(rs.getString("title"));
        project.setStartDate(rs.getDate("sdt"));
        project.setEndDate(rs.getDate("edt"));

        Member owner = new Member();
        owner.setNo(rs.getInt("owner_no"));
        owner.setName(rs.getString("owner_name"));
        project.setOwner(owner);

        project.setMembers(findAllMembers(project.getNo()));

        list.add(project);
      }
    }
    return list;
  }

  @Override
  public Project findByNo(int no) throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(
        "select"
            + "    p.no,"
            + "    p.title,"
            + "    p.content,"
            + "    p.sdt,"
            + "    p.edt,"
            + "    m.no as owner_no,"
            + "    m.name as owner_name"
            + "  from pms_project p"
            + "    inner join pms_member m on p.owner=m.no"
            + " where p.no=?")) {

      stmt.setInt(1, no);

      try (ResultSet rs = stmt.executeQuery()) {
        if (!rs.next()) {
          return null;
        }

        Project project = new Project();
        project.setNo(rs.getInt("no"));
        project.setTitle(rs.getString("title"));
        project.setStartDate(rs.getDate("sdt"));
        project.setEndDate(rs.getDate("edt"));

        Member owner = new Member();
        owner.setNo(rs.getInt("owner_no"));
        owner.setName(rs.getString("owner_name"));
        project.setOwner(owner);

        project.setMembers(findAllMembers(project.getNo()));

        return project;
      }
    }
  }

  @Override
  public int update(Project project) throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(
        "update pms_project set"
            + " title=?,"
            + " content=?,"
            + " sdt=?,"
            + " edt=?,"
            + " owner=?"
            + " where no=?")) { 

      con.setAutoCommit(false);

      stmt.setString(1, project.getTitle());
      stmt.setString(2, project.getContent());
      stmt.setDate(3, project.getStartDate());
      stmt.setDate(4, project.getEndDate());
      stmt.setInt(5, project.getOwner().getNo());
      stmt.setInt(6, project.getNo());
      int count = stmt.executeUpdate();

      // 기존 프로젝트의 모든 멤버를 삭제한다.
      deleteMembers(project.getNo());

      // 프로젝트 멤버를 추가한다.
      for (Member member : project.getMembers()) {
        insertMember(project.getNo(), member.getNo());
      }

      con.commit();

      return count;

    } catch (Exception e) {
      con.rollback();

      // 이 catch 블록의 목적은 예외를 처리 하는 것이 아니라,
      // rollback을 실행하는 것이다.
      // 따라서 예외가 발생한 사실은 이전처럼 호출자에게 그대로 보고해야 한다.
      throw e; // 호출자가 알 수 있도록 예외 던지기

    } finally {
      con.setAutoCommit(true);
    }
  }

  @Override
  public int delete(int no) throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(
        "delete from pms_project where no=?")) {

      con.setAutoCommit(false);

      // 프로젝트에 소속된 팀원 정보 삭제
      deleteMembers(no);

      // 프로젝트 정보 삭제
      stmt.setInt(1, no);
      int count = stmt.executeUpdate();
      con.commit();  

      return count;

    } catch (Exception e) {
      con.rollback();

      // 이 catch 블록의 목적은 예외를 처리 하는 것이 아니라,
      // rollback을 실행하는 것이다.
      // 따라서 예외가 발생한 사실은 이전처럼 호출자에게 그대로 보고해야 한다.
      throw e; // 호출자가 알 수 있도록 예외 던지기

    } finally {
      con.setAutoCommit(true);
    }
  }

  @Override
  public int insertMember(int projectNo, int memberNo) throws Exception {
    try (PreparedStatement stmt = con.prepareStatement(
        "insert into pms_member_project(member_no,project_no) values(?,?)")) {

      stmt.setInt(1, memberNo);
      stmt.setInt(2, projectNo);
      return stmt.executeUpdate();
    }
  }

  @Override
  public List<Member> findAllMembers(int projectNo) throws Exception {
    // 프로젝트에 참여하고 있는 멤버를 리스트로 리턴
    // 비어있어도 리스트를 리턴 -> null은 없다.
    ArrayList<Member> list = new ArrayList<>();

    try (PreparedStatement stmt = con.prepareStatement(
        "select"
            + "    m.no,"
            + "    m.name"
            + "  from pms_member_project mp"
            + "    inner join pms_member m on mp.member_no=m.no"
            + "  where"
            + "    mp.project_no=?")) {

      stmt.setInt(1, projectNo);

      try (ResultSet memberRs = stmt.executeQuery()) {
        while (memberRs.next()) {
          Member m = new Member();
          m.setNo(memberRs.getInt("no"));
          m.setName(memberRs.getString("name"));
          list.add(m);
        }
      }
    }
    return list;
  }

  @Override
  public int deleteMembers(int projectNo) throws Exception {
    // 들어있던 멤버를 다 지워버리고 새로 다 넣어버리는 것
    try (PreparedStatement stmt = con.prepareStatement(
        "delete from pms_member_project where project_no=?")) {
      stmt.setInt(1, projectNo);
      return stmt.executeUpdate();
    }
  }
}