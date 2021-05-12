package com.eomcs.pms.service.impl;

import java.util.List;
import com.eomcs.pms.dao.JourneyDao;
import com.eomcs.pms.domain.Journey;
import com.eomcs.pms.service.JourneyService;

public class DefaultJourneyService implements JourneyService {

  JourneyDao journeyDao; 

  public DefaultJourneyService(JourneyDao journeyDao) {
    this.journeyDao = journeyDao;
  }

  // 게시글 등록 업무
  @Override
  public int add(Journey journey) throws Exception {
    return journeyDao.insert(journey);
  }

  // 게시글 목록 조회 업무
  @Override
  public List<Journey> list() throws Exception {
    return journeyDao.findByKeyword(null);
  }

  // 게시글 상세 조회 업무
  @Override
  public Journey get(int no) throws Exception {
    Journey journey = journeyDao.findByNo(no);
    return journey; 
  }

  // 게시글 변경 업무
  @Override
  public int update(Journey journey) throws Exception {
    return journeyDao.update(journey);
  }

  // 게시글 삭제 업무
  @Override
  public int delete(int no) throws Exception {
    return journeyDao.delete(no);
  }

  // 게시글 검색 업무
  @Override
  public List<Journey> search(String keyword) throws Exception {
    return journeyDao.findByKeyword(keyword);
  }
}







