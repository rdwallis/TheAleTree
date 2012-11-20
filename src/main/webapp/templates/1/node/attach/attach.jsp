<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
	<c:when test="${model.parentId != null}">
		<c:set var="parentId" value="${model.parentId}" />
		<c:set var="showLink" value="${model.showLink}" />
		<c:set var="showComment" value="${model.showComment}" />
		<c:set var="open" value="${model.open}" />
	</c:when>
	<c:otherwise>
		<c:set var="showLink" value="true" />
		<c:set var="showComment" value="true" />
		<c:set var="open" value="false" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${parentId == 0}">
		<c:set var="submitText" value="Submit a Link" />
		<c:set var="commentText" value="Post a Comment" />
	</c:when>
	<c:otherwise>
		<c:set var="submitText" value="Attach a Link" />
		<c:set var="commentText" value="Attach a Comment" />
	</c:otherwise>
</c:choose>
<c:if test="${parentId != 0}">
<div class="attachPanel">
</c:if>

<c:if test="${showLink}">
		<ex:DisclosureTag href="/1/node/${parentId}/attach?showcomment=false" text="${submitText}" open="${open}">
			<%@ include file="attachLink.jsp"%>
		</ex:DisclosureTag>
</c:if>
<c:if test="${showComment}">
		<ex:DisclosureTag href="/1/node/${parentId}/attach?showlink=false" text="${commentText}" open="${open}">
			<%@ include file="attachComment.jsp"%>
		</ex:DisclosureTag>
</c:if>
<c:if test="${parentId != 0}">
	</div>
</c:if>



