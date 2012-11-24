<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="nodeHeader.jsp"%>
<c:choose>
	<c:when test="${node.childCount == 0}">
	
		<ex:DisclosureTag href="/1/node/${node.id}/attach" text="Attach A New Branch">
			<c:set var="parentId" value="${node.id}" scope="request" />		
			<%@ include file="attach/attach.jsp"%>
		</ex:DisclosureTag>
	</c:when>
	<c:otherwise>
		<ex:DisclosureTag href="/1/node/${node.id}" text="${node.childCount} ${node.childCount == 1 ? \"branch\" : \"branches\" }" />
	</c:otherwise>
</c:choose>
<%@ include file="nodeFooter.jsp"%>

