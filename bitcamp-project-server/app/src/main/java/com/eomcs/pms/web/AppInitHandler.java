package com.eomcs.pms.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.eomcs.mybatis.MybatisDaoFactory;
import com.eomcs.mybatis.SqlSessionFactoryProxy;
import com.eomcs.mybatis.TransactionManager;
import com.eomcs.pms.dao.BoardDao;
import com.eomcs.pms.dao.MemberDao;
import com.eomcs.pms.dao.ProjectDao;
import com.eomcs.pms.dao.TaskDao;
import com.eomcs.pms.service.BoardService;
import com.eomcs.pms.service.MemberService;
import com.eomcs.pms.service.ProjectService;
import com.eomcs.pms.service.TaskService;
import com.eomcs.pms.service.impl.DefaultBoardService;
import com.eomcs.pms.service.impl.DefaultMemberService;
import com.eomcs.pms.service.impl.DefaultProjectService;
import com.eomcs.pms.service.impl.DefaultTaskService;

// 톰캣서버를 실행 후 제일 처음에 한번 실행해야 한다.
// 그렇지 않으면 NullpointException 에러가 발생된다.
@WebServlet("/init") 
public class AppInitHandler implements Servlet {

  @Override
  public void init(ServletConfig config) throws ServletException {

  }

  @Override
  public void destroy() {

  }

  @Override
  public ServletConfig getServletConfig() {
    return null;
  }

  @Override
  public String getServletInfo() {
    return null;
  }

  @Override
  public void service(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {

    // 1) Mybatis 관련 객체 준비 
    InputStream mybatisConfigStream = Resources.getResourceAsStream(
        "com/eomcs/pms/conf/mybatis-config.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigStream);
    SqlSessionFactoryProxy sqlSessionFactoryProxy = new SqlSessionFactoryProxy(sqlSessionFactory);
    MybatisDaoFactory daoFactory = new MybatisDaoFactory(sqlSessionFactoryProxy);

    // 2) DAO 관련 객체 준비
    BoardDao boardDao = daoFactory.createDao(BoardDao.class);
    MemberDao memberDao = daoFactory.createDao(MemberDao.class);
    ProjectDao projectDao = daoFactory.createDao(ProjectDao.class);
    TaskDao taskDao = daoFactory.createDao(TaskDao.class);

    // 3) 서비스 관련 객체 준비
    TransactionManager txManager = new TransactionManager(sqlSessionFactoryProxy);

    BoardService boardService = new DefaultBoardService(boardDao);
    MemberService memberService = new DefaultMemberService(memberDao);
    ProjectService projectService = new DefaultProjectService(txManager, projectDao, taskDao);
    TaskService taskService = new DefaultTaskService(taskDao);

    // 4) 서비스 객체를 ServletContext 보관소에 저장한다.
    ServletContext servletContext = request.getServletContext();

    servletContext.setAttribute("boardService", boardService);
    servletContext.setAttribute("memberService", memberService);
    servletContext.setAttribute("projectService", projectService);
    servletContext.setAttribute("taskService", taskService);

    response.setContentType("text/plain;charset=UTF-8"); // 한글이 깨지지 않게 하기 위함
    PrintWriter out = response.getWriter();
    out.println("의존 객체를 모두 준비하였습니다.");
  }

}
