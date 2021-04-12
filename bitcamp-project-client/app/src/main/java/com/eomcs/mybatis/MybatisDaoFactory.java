package com.eomcs.mybatis;

import java.lang.reflect.Proxy;

public class MybatisDaoFactory {

  // DAO 인터페이스를 구현한 객체를 만들어준다.
  @SuppressWarnings("unchecked")
  public <T> T createDao(Class<T> daoInterface) { // Class<T>의 T라는 자리에 BoardDao가 오면 앞의 T에 BoardDao가 놓여 리턴된다.  // 타입파라미터
    return (T) Proxy.newProxyInstance( // 원래는 Object 타입이지만 T 타입을 원하기 때문에 형변환!
        this.getClass().getClassLoader(), // 클래스 정보 알아내기 -> 클래스를 로딩한 클래스로더의 정보를 리턴 
        new Class<?> [] {daoInterface}, // 구현할 인터페이스 (배열로)
        null);

    // new Class<?> [] {daoInterface}의 형태
    //    Class[] types = new Class[3];
    //    types[0] = MemberDao.class;
    //    types[1] = String.class;
    //    types[2] = Object.class;
    //    
    //    Class<?>[] types2 = new Class<?>[] {MemberDao.class, String.class, Object.class};
    // types1과 types2는 같다.


  }
}
