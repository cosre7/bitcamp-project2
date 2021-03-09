package com.eomcs.pms.table;

import java.io.DataOutputStream;
import java.io.File;
import java.util.List;
import com.eomcs.pms.domain.Board;

// 1) 간다난 동작 테스트를 위해 임의의 값을 리턴한다.
// 2) JSON 포맷의 파일을 로딩한다.
public class BoardTable extends AbstractJsonDataTable<Board> {

  public BoardTable() {
    super(new File("boards.json"), Board.class);

    // 수퍼 클래스에서 설정한 대로 해당 JSON 파일을 읽어 도메인 객체를 만든 후 컬렉션에 보관한다.
    this.loadJsonData();
  }

  @Override
  public void service(String command, List<String> data, DataOutputStream out) throws Exception {
    switch (command) {
      case "board/insert":
        out.writeUTF("success");
        out.writeInt(1);
        out.writeUTF("입력 성공!");
        break;
      case "board/selectall":
        out.writeUTF("success");
        out.writeInt(3);
        out.writeUTF("목록!");
        out.writeUTF("목록!");
        out.writeUTF("목록!");
        break;
      case "board/select":
        out.writeUTF("success");
        out.writeInt(1);
        out.writeUTF("상세 정보!");
        break;
      case "board/update":
        out.writeUTF("success");
        out.writeInt(0);
        break;
      case "board/delete":
        out.writeUTF("success");
        out.writeInt(0);
        break;
      default:
        out.writeUTF("error");
        out.writeInt(0);
        out.writeUTF("해당 명령을 처리할 수 없습니다.");
    }
  }
}
