package com.eomcs.pms;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.eomcs.mybatis.MybatisDaoFactory;
import com.eomcs.pms.dao.BoardDao;
import com.eomcs.pms.dao.MemberDao;
import com.eomcs.pms.dao.ProjectDao;
import com.eomcs.pms.dao.TaskDao;
import com.eomcs.pms.handler.Command;
import com.eomcs.pms.service.BoardService;
import com.eomcs.pms.service.MemberService;
import com.eomcs.pms.service.ProjectService;
import com.eomcs.pms.service.TaskService;
import com.eomcs.pms.service.impl.DefaultBoardService;
import com.eomcs.pms.service.impl.DefaultMemberService;
import com.eomcs.pms.service.impl.DefaultProjectService;
import com.eomcs.pms.service.impl.DefaultTaskService;
import com.eomcs.stereotype.Component;
import com.eomcs.util.CommandRequest;
import com.eomcs.util.CommandResponse;

public class ServerApp {

  int port;

  // 서버의 상태를 설정
  boolean isStop;

  //객체를 보관할 컨테이너 준비
  Map<String,Object> objMap = new HashMap<>();

  public static void main(String[] args) {

    try {
      ServerApp app = new ServerApp(8888);
      app.service();

    } catch (Exception e) {
      System.out.println("서버를 시작 하는 중에 오류 발생!");
      e.printStackTrace();
    }
  }

  public ServerApp(int port) {
    this.port = port;
  }

  public void service() throws Exception { // service -> main 스레드가 호출하는 메서드

    // 스레드풀 준비
    ExecutorService threadPool = Executors.newCachedThreadPool();

    // 1) Mybatis 프레임워크 관련 객체 준비
    // => Mybatis 설정 파일을 읽을 입력 스트림 객체 준비
    InputStream mybatisConfigStream = Resources.getResourceAsStream(
        "com/eomcs/pms/conf/mybatis-config.xml");

    // => SqlSessionFactory 객체 준비
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigStream);

    // => DAO가 사용할 SqlSession 객체 준비
    //    - 수동 commit 으로 동작하는 SqlSession 객체를 준비한다.
    SqlSession sqlSession = sqlSessionFactory.openSession(false);// Mybatis 설정 파일을 읽을 입력 스트림 객체 준비

    // 2) DAO 구현체를 자동으로 만들어주는 공장 객체를 준비한다.
    MybatisDaoFactory daoFactory = new MybatisDaoFactory(sqlSession);

    // 3) 서비스 객체가 사용할 DAO 객체 준비
    BoardDao boardDao = daoFactory.createDao(BoardDao.class);
    MemberDao memberDao = daoFactory.createDao(MemberDao.class);
    ProjectDao projectDao = daoFactory.createDao(ProjectDao.class);
    TaskDao taskDao = daoFactory.createDao(TaskDao.class);

    // 4) Command 구현체가 사용할 의존 객체(서비스 객체 + 도우미 객체) 준비
    // => 서비스 객체 생성
    BoardService boardService = new DefaultBoardService(sqlSession, boardDao);
    MemberService memberService = new DefaultMemberService(sqlSession, memberDao);
    ProjectService projectService = new DefaultProjectService(sqlSession, projectDao, taskDao);
    TaskService taskService = new DefaultTaskService(sqlSession, taskDao);

    // => 도우미 객체 생성
    //    MemberValidator memberValidator = new MemberValidator(memberService);

    // => Command 구현체가 사용할 의존 객체를 보관
    objMap.put("boardService", boardService);
    objMap.put("memberService", memberService);
    objMap.put("projectService", projectService);
    objMap.put("taskService", taskService);
    //    objMap.put("memberValidator", memberValidator);

    // 5) Command 구현체를 자동 생성하여 맵에 등록
    registerCommands();

    // 클라이언트 연결을 기다리는 서버 소켓 생성
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {

      System.out.println("서버 실행!");

      while (true) {
        Socket socket = serverSocket.accept(); // 클라이언트가 접속

        if (isStop) { // 서버의 상태가 종료이면,
          break; // 즉시 반복문을 탈출하여 메인 스레드의 실행을 끝낸다.
        }

        threadPool.execute(() -> processRequest(socket)); // 러너블 인터페이스 구현체(run 메서드) : () -> processRequest(socket) => task객체
      }

    } catch (Exception e) {
      System.out.println("서버 실행 중 오류 발생!");
      e.printStackTrace();
    }

    // 스레드풀에 대기하고 있는 모든 스레드를 종료시킨다.
    // => 단, 현재 실행 중인 스레드에 대해서는 작업을 완료한 후 종료하도록 설정한다.
    threadPool.shutdown(); // 스레드 풀에서 대기하고 있는 스레드만 종료시킨다.
    System.out.println("서버 종료 중...");

    // 만약 현재 실행 중인 스레드를 강제로 종료시키고 싶다면 
    // 다음 코드를 참고하라!
    // 10초 동안 기다려도 종료가 안됬는지 다시 확인!
    try {
      if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
        System.out.println("아직 실행 중인 스레드가 있습니다.");

        // 종료를 재시도한다.
        // => 대기 중인 작업도 취소한다.
        // => 실행 중인 스레드 중에서 Not Runnable 상태에 있을 경우에도 강제로 종료시킨다.
        threadPool.shutdownNow();

        while (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
          System.out.println("아직 실행 중인 스레드가 있습니다.");
        } 

        System.out.println("모든 스레드를 종료했습니다.");
      }
    } catch (Exception e) {
      System.out.println("스레드 강제 종료 중에 오류 발생!");
    }
    System.out.println("서버 종료!");
  }

  public void processRequest(Socket socket) {
    try (
        Socket clientSocket = socket; 
        // socket.close를 여기에서 처리하기 위해서 Socket을 여기 다시 선언!
        // 아니면 auto close되지않는다! 
        // try 블럭을 나가기 전에 socket.close를 하기 위함!
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        ) {

      while (true) {
        // 클라이언트가 보낸 요청을 읽는다.
        String requestLine = in.readLine();

        // 클라이언트가 보낸 나머지 데이터를 읽는다.
        while (true) {
          String line = in.readLine();
          if (line.length() == 0) {
            break;
          }
          // 지금은 '요청 명령'과 '빈 줄' 사이에 존재하는 데이터는 무시한다. 
        }

        if (requestLine.equalsIgnoreCase("serverstop")) {
          out.println("Server stopped!");
          out.println();
          out.flush();
          terminate(); // terminate: 끝내다
          return;
        }

        if (requestLine.equalsIgnoreCase("exit") || requestLine.equalsIgnoreCase("quit")) {
          out.println("Goodbye!");
          out.println();
          out.flush();
          return;
        }

        // 클라이언트의 요청을 처리할 Command 구현체를 찾는다.
        Command command = (Command) objMap.get(requestLine);
        if (command == null) {
          out.println("해당 명령을 처리할 수 없습니다!");
          out.println();
          out.flush();
          continue;
        }

        //        class 차량판매점 {
        //          public Car getCar();
        //        }
        // 차량판매점 -> 향후 승용차, 탱크, 공중부양차 등등의 가능성에 대비해서
        // 추상클래스인 getCar()를 리턴하도록 정의되어있다.
        //        
        //        승용차 c = (승용차)차량판매점.getCar(); // 일반적인 경우
        //        탱크 c2 = (탱크)차량판매점.getCar(); // 군대

        // 클라이언트가 보낸 명령을 Command 구현체에게 전달하기 쉽도록 객체에 담는다.
        InetSocketAddress remoteAddr = (InetSocketAddress) clientSocket.getRemoteSocketAddress(); 
        // 원래 리턴 타입은 SocketAddress (추상클래스)
        // 일반적으로 인터넷을 사용하기 때문에 InetSocketAddress(SocketAddress를 상속)로 사용한다.
        // 인터넷 통신 프로그램이 아닌 특수한 경우에는 이 코드가 동작하지 않는다.
        // 하지만 그럴 일은 거의 없다.

        CommandRequest request = new CommandRequest(
            requestLine,
            remoteAddr.getHostString(),
            remoteAddr.getPort());

        CommandResponse response = new CommandResponse(out);

        // Command 구현체를 실행한다.
        try {
          command.service(request, response);
        } catch (Exception e) {
          out.println("서버 오류 발생!");
        }
        out.println();
        out.flush();
      }

    } catch (Exception e) {
      System.out.println("클라이언트의 요청을 처리하는 중에 오류 발생!");
      e.printStackTrace();
    }
  }

  // 서버를 최종적으로 종료하는 일을 한다.
  private void terminate() {
    // 서버의 상태를 종료로 설정한다.
    isStop = true;

    // 그리고 서버가 즉시 종료할 수 있도록 임의의 접속을 수행한다.
    // => 스스로 클라이언트가 되어 ServerSocket에 접속하면
    //    accept() 에서 리턴하기 때문에 isStop 변수의 상태에 따라 반복문을 멈출 것이다.
    // 서버에 접속된 임의의 상태를 만든 후에 서버의 상태를 종료시키기 위함
    // 클라이언트 앱 실행 후 명령에 serverstop을 해서 서버 종료! 시키기 위함 -> isStop 이 true가 되는 상황
    try (Socket socket = new Socket("localhost", 8888)) {
      // 서버를 종료시키기 위해 임의로 접속하는 것이기 때문에 특별히 추가로 해야 할 일이 없다.
      // 하지만 메인 스레드가 종료되고 다른 스레들이 남아있기 때문에 서버가 종료되고 난 후에도
      // Console 자체는 종료되지 않고 남아있다 -> 강제종료해야한다.
    } catch (Exception e) {}
  }

  private void registerCommands() throws Exception {

    // 패키지에 소속된 모든 클래스의 타입 정보를 알아낸다.
    ArrayList<Class<?>> components = new ArrayList<>();
    loadComponents("com.eomcs.pms.handler", components);

    for (Class<?> clazz : components) {

      // 클래스 목록에서 클래스 정보를 한 개 꺼내, Command 구현체인지 검사한다.
      if (!isCommand(clazz)) {
        continue;
      }

      // 클래스 정보를 이용하여 객체를 생성한다.
      Object command = createCommand(clazz);

      // 클래스 정보에서 @Component 애노테이션 정보를 가져온다.
      Component compAnno = clazz.getAnnotation(Component.class);

      // 애노테이션 정보에서 맵에 객체를 저장할 때 키로 사용할 문자열 꺼낸다.
      String key = null;
      if (compAnno.value().length() == 0){
        key = clazz.getName(); // 키로 사용할 문자열이 없으면 클래스 이름을 키로 사용한다.
      } else {
        key = compAnno.value();
      }

      // 생성된 객체를 객체 맵에 보관한다.
      objMap.put(key, command);

      System.out.println("인스턴스 생성 ===> " + command.getClass().getName());
    }
  }

  private boolean isCommand(Class<?> type) {
    // 클래스가 아니라 인터페이스라면 무시한다.
    if (type.isInterface()) {
      return false;
    }

    // 클래스의 인터페이스 목록을 꺼낸다.
    Class<?>[] interfaces = type.getInterfaces();

    // 클래스가 구현한 인터페이스 중에서 Command 인터페이스가 있는지 조사한다.
    for (Class<?> i : interfaces) {
      if (i == Command.class) {
        return true;
      }
    }

    return false;
  }

  private void loadComponents(String packageName, ArrayList<Class<?>> components) throws Exception {

    // 패키지의 '파일 시스템 경로'를 알아낸다.
    File dir = Resources.getResourceAsFile(packageName.replaceAll("\\.", "/"));

    if (!dir.isDirectory()) {
      throw new Exception("유효한 패키지가 아닙니다.");
    }

    File[] files = dir.listFiles(f -> {
      if (f.isDirectory() || f.getName().endsWith(".class"))
        return true;
      return false;
    });

    for (File file : files) {
      if (file.isDirectory()) {
        loadComponents(packageName + "." + file.getName(), components);
      } else {
        String className = packageName + "." + file.getName().replace(".class", "");
        try {
          Class<?> clazz = Class.forName(className);
          if (clazz.getAnnotation(Component.class) != null) {
            components.add(clazz);
          }
        } catch (Exception e) {
          System.out.println("클래스 로딩 오류: " + className);
        }
      }
    }
  }

  private Object createCommand(Class<?> clazz) throws Exception {
    // 생성자 정보를 알아낸다. 첫 번째 생성자만 꺼낸다.
    Constructor<?> constructor = clazz.getConstructors()[0];

    // 생성자의 파라미터 정보를 알아낸다.
    Parameter[] params = constructor.getParameters();

    // 생성자를 호출할 때 넘겨 줄 값을 담을 컬렉션을 준비한다.
    ArrayList<Object> args = new ArrayList<>();

    // 각 파라미터의 타입을 알아낸 후 objMap에서 찾는다.
    for (Parameter p : params) {
      Class<?> paramType = p.getType();
      args.add(findDependency(paramType));
    }

    // 생성자를 호출하여 인스턴스를 생성한다.
    return constructor.newInstance(args.toArray());
  }

  private Object findDependency(Class<?> type) {
    // 맵에서 값 목록을 꺼낸다.
    Collection<?> values = objMap.values();

    for (Object obj : values) {
      if (type.isInstance(obj)) {
        return obj;
      }
    }
    return null;
  }
}