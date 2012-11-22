<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<div class="node">
	<div class="vote">
		<a href="/1/node/${node.id}/up" onclick="node.vote(${node.id}, true); return false;">
		<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 50 50" class="arrow">
			<polyline points="0,50  25,0  50,50" class="unselected"  id="up${node.id}" />
			<line x1="25" y1="0" x2="50" y2="50" style="stroke: black; stroke-width:1;" />
			<line x1="0" y1="50" x2="50" y2="50" style="stroke: black; stroke-width:1;" />
		</svg>
		</a>
		<div id="vCount${node.id}" class="count" title="${node.up} up | ${node.down} down">${node.up - node.down}</div>
		<a href="/1/node/${node.id}/down" onclick="node.vote(${node.id}, false); return false;">
		<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 50 50" class="arrow">
   			<polyline points="0,0  25,50  50,0" class="unselected" id="down${node.id}"/>
   			<line x1="25" y1="50" x2="50" y2="0" style="stroke: black; stroke-width:1;" />
		</svg>
		</a>
		

	</div>
	<div class="right">
		<div class="content">
			<c:choose>
				<c:when test="${not empty node.url}">
					<h2>
						<a href="${node.url}">${node.title}</a>
					</h2>
				</c:when>
				<c:otherwise>
						${node.comment}
						</c:otherwise>
			</c:choose>
		</div>
		<div class="children">
			<div class="age">
					Posted <ex:PrettyTimeTag date="${node.created}"/> 
					<c:if test="${not empty node.url}">
						from <ex:HostStripTag url="${node.url}"/>
					</c:if>
			</div>
		


