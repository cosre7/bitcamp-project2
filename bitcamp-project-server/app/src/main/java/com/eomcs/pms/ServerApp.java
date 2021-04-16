package com.eomcs.pms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.eomcs.util.concurrent.ThreadPool;

public class ServerApp {

  int port;

  // 서버의 상태를 설정
  boolean isStop;

  public static void main(String[] args) {
    ServerApp app = new ServerApp(8888);
    app.service();
  }

  public ServerApp(int port) {
    this.port = port;
  }

  public void service() { // service -> main 스레드가 호출하는 메서드

    // 스레드풀 준비
    ThreadPool threadPool = new ThreadPool();

    // 클라이언트 연결을 기다리는 서버 소켓 생성
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {

      System.out.println("서버 실행!");

      while (true) {
        Socket socket = serverSocket.accept(); // 클라이언트가 접속

        if (isStop) { // 서버의 상태가 종료이면,
          break; // 즉시 반복문을 탈출하여 메인 스레드의 실행을 끝낸다.
        }

        threadPool.execute(() -> processRequest(socket)); // 러너블 인터페이스 구현체 : () -> processRequest(socket) => task객체
      }

    } catch (Exception e) {
      System.out.println("서버 실행 중 오류 발생!");
      e.printStackTrace();
    }

    // 스레드풀을 모든 스레드를 종료시킨다.
    // => 단, 현재 접속 중인 스레드에 대해서는 작업을 완료할 때까지 기다린다.
    threadPool.shutdown();

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

        if (requestLine.equalsIgnoreCase("serverstop")) {
          in.readLine(); // 요청의 끝을 의미하는 빈 줄을 읽는다.
          out.println("Server stopped!");
          out.println();
          out.flush();
          terminate(); // terminate: 끝내다
          return;
        }

        if (requestLine.equalsIgnoreCase("exit") || requestLine.equalsIgnoreCase("quit")) {
          // 읽은게 exit나 quit라면 goodbye 출력하고 끝내기
          in.readLine(); // 요청의 끝을 의미하는 빈 줄을 읽는다.
          out.println("Goodbye!");
          out.println();
          out.flush();
          return;
        }

        // 클라이언트가 보낸 명령을 서버 창에 출력한다.
        System.out.println(requestLine);

        // 클라이언트가 보낸 데이터를 읽는다.
        while (true) {
          String line = in.readLine();
          if (line.length() == 0) {
            break; // 데이터 읽다가 빈줄 읽으면 끝
          }
          // 클라이언트에서 보낸 데이터를 서버 창에 출력해 보자.
          System.out.println(line);
        }
        System.out.println("----------------------------------------------");

        // 클라이언트에게 응답한다.
        out.println("OK");
        out.printf("====> %s\n", requestLine);
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
}