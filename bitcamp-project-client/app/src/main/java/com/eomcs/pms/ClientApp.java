package com.eomcs.pms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import com.eomcs.util.Prompt;

public class ClientApp {

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

    // 서버와 연결한다.
    try (Socket socket = new Socket(this.serverAddress, this.port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream())) {

      while (true) {
        String message = Prompt.inputString("명령> ");

        // 1) 명령어를 보낸다.
        out.writeUTF(message);

        // 2) 서버에 보낼 데이터의 개수를 보낸다.
        out.writeInt(3);

        // 3) 서버에 데이터를 보낸다.
        out.writeUTF("aaaa");
        out.writeUTF("bbbb");
        out.writeUTF("cccc");

        out.flush();

        String response = in.readUTF();
        System.out.println(response);

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
