<%@ page language="java" 
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach items="${members}" var="m">
  <c:if test="${not empty projectMembers}">
    <%-- ==> ${m.no}  오류 확인용 --%>
	  <c:forEach items="${projectMembers}" var="projectMember">
	    <%-- xxx> ${projectMember.no}  오류 확인용 --%>
	    <c:if test="${m.no == projectMember.no}">
	      <c:set var="checked" value="checked"/>
	    </c:if>
	  </c:forEach>
  </c:if>
  <input type='checkbox' name='member' value='${m.no}' %{checked}>${m.name}<br>
  <c:remove var="checked"/> <%-- 안하면 전체 선택 --%>
</c:forEach>
