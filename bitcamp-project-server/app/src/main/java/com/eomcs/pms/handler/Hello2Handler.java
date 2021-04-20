package com.eomcs.pms.handler;

import java.io.PrintWriter;
import com.eomcs.stereotype.Component;
import com.eomcs.util.CommandRequest;
import com.eomcs.util.CommandResponse;

@Component("/haha")
public class Hello2Handler implements Command {

  @Override
  public void service(CommandRequest request, CommandResponse response) throws Exception {
    PrintWriter out = response.getWriter();

    out.println("오호라.. 안녕!!!");

  }

}
