<%@ taglib prefix="ex" uri="/WEB-INF/lib/custom.tld"%>
<div class="attachPanel">
	<form action="/1/node/${parentId}/create" method="GET" class="comment">
		<textarea name="comment"></textarea>
		<input type="submit" value="Post Comment" />
		<ex:DisclosureTag href="/docs/markdownguide.html" text="Simple Markdown Guide"></ex:DisclosureTag>
	</form>
</div>
