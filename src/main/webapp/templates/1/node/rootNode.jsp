<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<%@ include file="nodeHeader.jsp" %>
	<ex:DisclosureTag href="/1/node/${node.id}" text="${node.childCount} branches" open="true">
		<%@ include file="nodeList.jsp" %>
	</ex:DisclosureTag>
<%@ include file="nodeFooter.jsp" %>


