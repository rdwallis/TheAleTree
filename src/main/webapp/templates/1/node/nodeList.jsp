<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<c:if test="${model.root != null}">
	<ex:DisclosureTag href="/1/node/${model.root.id}/attach" text="Attach A New Branch"/>
</c:if>
<c:if test="${model.children != null }">

	<c:forEach var="node" items="${model.children}">
		<c:set var="node" value="${node}" scope="request"/>
		<%@ include file="node.jsp" %>
	</c:forEach>
</c:if>


