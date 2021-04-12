package com.eomcs.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoWorker implements InvocationHandler {

  @Override
  public Object invoke(Object daoProxy, Method method, Object[] args) throws Throwable {
    // proxy가 만들어준 DAO 구현체가 호출하는 메서드다.
    System.out.printf("%s.%s() 호출됨!\n", 
        daoProxy.getClass().getInterfaces()[0].getName(), // 어떤 인터페이스를 구현했는지 알아내기
        method.getName()); // 어떤 메서드를 구현했는지 알아내기
    // /board/list => com.eomcs.pms.dao.BoardDao.findByKeyword() 호출됨!
    return null;
  }

}
