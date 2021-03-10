package com.eomcs.pms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import com.eomcs.pms.handler.BoardListHandler;
import com.eomcs.pms.handler.Command;
import com.eomcs.util.Prompt;

public class ClientApp {

  //사용자가 입력한 명령을 저장할 컬렉션 객체 준비
  ArrayDeque<String> commandStack = new ArrayDeque<>();
  LinkedList<String> commandQueue = new LinkedList<>();

  String serverAddress;
  int port;

  public static void main(String[] args) {
    ClientApp app = new ClientApp("localhost", 8888);
    app.excute();
  }

  public ClientApp(String serverAddress, int port) {
    this.serverAddress = serverAddress;
    this.port = port;
  }

  public void excute() {

    // 사용자 명령을 처리하는 객체를 맵에 보관한다.
    HashMap<String,Command> commandMap = new HashMap<>();

    //    commandMap.put("/board/add", new BoardAddHandler(boardList));
    commandMap.put("/board/list", new BoardListHandler(boardList));
    //    commandMap.put("/board/detail", new BoardDetailHandler(boardList));
    //    commandMap.put("/board/update", new BoardUpdateHandler(boardList));
    //    commandMap.put("/board/delete", new BoardDeleteHandler(boardList));

    // 서버와 연결한다.
    try (Socket socket = new Socket(this.serverAddress, this.port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream())) {

      while (true) {
        // 1) 명령어를 보낸다.
        String message = Prompt.inputString("명령> ");
        out.writeUTF(message);

        // 2) 서버에 보낼 데이터의 개수를 보낸다.
        int no = Prompt.inputInt("개수> ");
        out.writeInt(no);

        if (no > 0) {
          // 3) 서버에 데이터를 보낸다.
          String parameter = Prompt.inputString("데이터> ");
          out.writeUTF(parameter);
        }
        out.flush();

        // 서버가 보낸 데이터를 읽는다.
        // 1) 작업 결과를 읽는다.
        String response = in.readUTF();

        // 2) 데이터의 개수를 읽는다.
        int length = in.readInt();

        // 3) 데이터의 개수 만큼 읽어 List 컬렉션에 보관한다.
        ArrayList<String> data = null;
        if (length > 0) {
          data = new ArrayList<>();
          for (int i = 0; i < length; i++) {
            data.add(in.readUTF());
          }
        }

        System.out.println("--------------------------------------");
        System.out.printf("작업 결과: %s\n", response);
        System.out.printf("데이터 개수: %d\n", length);
        if (data != null ) {
          System.out.println("데이터:");
          for (String str : data) {
            System.out.println(str);
          }
        }

        if (message.equals("quit")) {
          break;
        }
      }

      Prompt.close();

    } catch (Exception e) {
      System.out.println("서버와 통신 하는 중에 오류 발생!");
    }
  }
}
