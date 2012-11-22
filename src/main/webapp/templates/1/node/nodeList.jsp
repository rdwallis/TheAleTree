<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<c:if test="${model.root != null}">
	<ex:DisclosureTag href="/1/node/${model.root.id}/attach" text="Attach A New Branch"/>
</c:if>
<c:if test="${model.children != null }">
	<c:set var="count" value="0" scope="page" />
	<c:forEach var="node" items="${model.children}">
		<c:set var="count" value="${count + 1}" scope="page"/>
		<c:set var="node" value="${node}" scope="request"/>
		<%@ include file="node.jsp" %>
	</c:forEach>
	<c:if test="${count == model.limit}">
		<c:choose>
			<c:when test="${model.root.id != null}">
				<ex:DisclosureTag href="/1/node/${model.root.id}?offset=${model.offset + model.limit}&limit=${model.limit}" text="Show More"/>
			</c:when>
			<c:otherwise>
				<ex:DisclosureTag href="/1/node/0?offset=${model.offset + model.limit}&limit=${model.limit}" text="Show More"/>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>


