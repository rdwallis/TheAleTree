<%@ include file="header.jsp"%>
<c:choose>
	<c:when test="${model.root != null}">
		<c:set var="node" value="${model.root}" scope="request" />
		<%@ include file="rootNode.jsp"%>
	</c:when>
	<c:otherwise>
		<c:set var="parentId" value="0" scope="request"/>
		<%@ include file="attach/attach.jsp"%>
		<%@ include file="nodeList.jsp"%>
	</c:otherwise>
</c:choose>

<%@ include file="footer.jsp"%>