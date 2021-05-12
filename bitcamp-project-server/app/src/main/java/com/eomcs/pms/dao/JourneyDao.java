package com.eomcs.pms.dao;

import java.util.List;
import com.eomcs.pms.domain.Journey;

public interface JourneyDao {

  int insert(Journey journey) throws Exception;

  List<Journey> findByKeyword(String keyword) throws Exception;

  Journey findByNo(int no) throws Exception;

  int update(Journey journey) throws Exception;

  int delete(int no) throws Exception;
}












