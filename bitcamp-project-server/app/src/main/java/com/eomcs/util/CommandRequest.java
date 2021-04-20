package com.eomcs.util;

import java.util.Map;

// 클라이언트의 요청 정보를 다루는 역할
public class CommandRequest {

  private String commandPath;
  private String remoteAddr;
  private int remotePort;
  private Prompt prompt;
  private Map<String, Object> session;

  public CommandRequest(
      String commandPath, 
      String remoteAddr, 
      int remotePort, 
      Prompt prompt, 
      Map<String, Object> session) {

    this.commandPath = commandPath;
    this.remoteAddr = remoteAddr;
    this.remotePort = remotePort;
    this.prompt = prompt;
    this.session = session;
  }

  public String getCommandPath() {
    return commandPath;
  }

  public String getRemoteAddr() {
    return remoteAddr;
  }

  public int getRemotePort() {
    return remotePort;
  }

  public Prompt getPrompt() {
    return prompt;
  }

  public Map<String, Object> getSession() {
    return session;
  }
}
