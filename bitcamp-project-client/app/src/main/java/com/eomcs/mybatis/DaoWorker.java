package com.eomcs.mybatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import org.apache.ibatis.session.SqlSession;

public class DaoWorker implements InvocationHandler {

  SqlSession sqlSession;

  public DaoWorker(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public Object invoke(Object daoProxy, Method method, Object[] args) throws Throwable {
    // proxy가 만들어준 DAO 구현체가 호출하는 메서드다.

    System.out.printf("%s.%s() 호출됨!\n", 
        daoProxy.getClass().getInterfaces()[0].getName(), // 어떤 인터페이스를 구현했는지 알아내기
        method.getName()); // 어떤 메서드를 구현했는지 알아내기

    Parameter[] params = method.getParameters();
    for (Parameter p : params) {
      System.out.printf("  %s %s\n", p.getType().getName(), p.getName()); // 파라미터 타입 알아내기
    }

    System.out.printf("  ==> %s\n", method.getReturnType().getName()); // 메서드의 리턴타입 알아내기

    // 메서드가 호출됬을 때 어떤 것을 실행할지 알아내기
    if (method.getReturnType() == int.class || 
        method.getReturnType() == void.class) {
      System.out.println("insert/update/delete 실행");
    } else if (method.getReturnType() == List.class) {
      System.out.println("selectList 실행");
    } else {
      System.out.println("selectOne 실행");
    }

    System.out.println("--------------------------------------------------------");

    return null;
  }

}
