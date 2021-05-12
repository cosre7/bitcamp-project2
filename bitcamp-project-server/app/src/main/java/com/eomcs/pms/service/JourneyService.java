package com.eomcs.pms.service;

import java.util.List;
import com.eomcs.pms.domain.Journey;

public interface JourneyService {

  int add(Journey journey) throws Exception;

  List<Journey> list() throws Exception;

  Journey get(int no) throws Exception;

  int update(Journey journey) throws Exception;

  int delete(int no) throws Exception;

  List<Journey> search(String keyword) throws Exception;
}







