package com.eomcs.pms.domain;

public class Journey {
  private int rjno;
  private int mno;
  private int rno;
  private int mstat;
  private String content;
  private double dstar;
  private int dno;

  @Override
  public String toString() {
    return "Journey [rjno=" + rjno + ", mno=" + mno + ", rno=" + rno + ", mstat=" + mstat
        + ", content=" + content + ", dstar=" + dstar + ", dno=" + dno + "]";
  }

  public int getRjno() {
    return rjno;
  }
  public void setRjno(int rjno) {
    this.rjno = rjno;
  }
  public int getMno() {
    return mno;
  }
  public void setMno(int mno) {
    this.mno = mno;
  }
  public int getRno() {
    return rno;
  }
  public void setRno(int rno) {
    this.rno = rno;
  }
  public int getMstat() {
    return mstat;
  }
  public void setMstat(int mstat) {
    this.mstat = mstat;
  }
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public double getDstar() {
    return dstar;
  }
  public void setDstar(double dstar) {
    this.dstar = dstar;
  }
  public int getDno() {
    return dno;
  }
  public void setDno(int dno) {
    this.dno = dno;
  }


}
