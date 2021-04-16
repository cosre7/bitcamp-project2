package com.eomcs.pms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

  int port;

  public static void main(String[] args) {
    ServerApp app = new ServerApp(8888);
    app.service();
  }

  public ServerApp(int port) {
    this.port = port;
  }

  public void service() {
    // 클라이언트 연결을 기다리는 서버 소켓 생성
    try (ServerSocket serverSocket = new ServerSocket(this.port)) {

      System.out.println("서버 실행!");

      while (true) {
        Socket socket = serverSocket.accept(); // 클라이언트가 접속

        // 1. 중첩클래스 버전
        //        class MyRunnable implements Runnable {
        //          @Override
        //          public void run() {
        //            processRequest(socket);
        //          }
        //        }
        //        new Thread(new MyRunnable()).start();
        //
        // 2. 익명 클래스 버전 (이름이 없는 서브 클래스 = 익명클래스)
        // MyRunnable이라는 클래스를 단 한번만 new로 생성하고 끝낼 예정.
        // 심지어 메서드도 Runnable에 있는 메서드를 사용할 예정 
        // -> 굳이 서브 클래스를 만들 필요가 없을지도 모르겠다.. 싶을 때
        //    이름이 없는 서브 클래스를 만드는 것 -> 익명 클래스 (구현하거나 상속받거나)
        //
        //        Runnable r = new Thread(new Runnable() {
        //          @Override
        //          public void run() {
        //            processRequest(socket);
        //          }
        //        }
        //        new Thread(r).start();
        //
        // 2-1. 익명 클래스 변형 -> 확인
        //        new Thread(new Thread(new Runnable() {
        //        @Override
        //        public void run() {
        //          processRequest(socket);
        //        }
        //      }).start();
        //
        // 3. 람다 문법 버전
        //        new Thread(() -> processRequest(socket)).start();

        new Thread(() -> processRequest(socket)).start(); // 러너블 구현체를 포함한 자식 스레드 만들기
      }

    } catch (Exception e) {
      System.out.println("서버 실행 중 오류 발생!");
      e.printStackTrace();
    }
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

        if (requestLine.equalsIgnoreCase("exit") || requestLine.equalsIgnoreCase("quit")) {
          // 읽은게 exit나 quit라면 goodbye 출력하고 끝내기
          in.readLine(); // 요청의 끝을 의미하는 빈 줄을 읽는다.
          out.println("Goodbye!");
          out.println();
          out.flush();
          break;
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
}