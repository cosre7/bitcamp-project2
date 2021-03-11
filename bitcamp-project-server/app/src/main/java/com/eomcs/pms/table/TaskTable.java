package com.eomcs.pms.table;

import java.io.File;
import java.util.List;
import com.eomcs.pms.domain.Task;
import com.eomcs.util.JsonFileHandler;
import com.eomcs.util.Request;
import com.eomcs.util.Response;

public class TaskTable implements DataTable {

  File jsonFile = new File("tasks.json");
  List<Task> list;

  public TaskTable() {
    list = JsonFileHandler.loadObjects(jsonFile, Task.class);
  }

  @Override
  public void service(Request request, Response response) throws Exception {
    Task task = null;
    String[] fields = null;

    switch (request.getCommand()) {
      case "task/insert":
        break;
      case "task/selectall":
        break;
      case "task/select":
        break;
      case "task/update":
        break;
      case "task/delete":
        break;
    }
  }

  private Task getTask(int taskNo) {
    for (Task t : list) {
      if (t.getNo() == taskNo) {
        return t;
      }
    }
    return null;
  }

}
