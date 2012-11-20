<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<title><c:if test="${model.root != null}">${model.root.title} | </c:if>The
	Ale Tree</title>
	 <link rel="icon" href="images/ale_tree16x16.png" sizes="16x16" type="image/png">
	 <link rel="icon" href="images/ale_tree32x32.png" sizes="32x32" type="image/png">
	 <link rel="icon" href="images/ale_tree48x48.png" sizes="48x48" type="image/png">
	 <link rel="icon" href="images/ale_tree96x96.png" sizes="96x96" type="image/png">
	 <link rel="icon" href="images/ale_tree128x128.png" sizes="128x128" type="image/png">
	 <link rel="icon" href="images/ale_tree256x256.png" sizes="256x256" type="image/png">
	 <link rel="icon" href="images/ale_tree.svg" sizes="any" type="image/svg+xml">
	
	<style type="text/css">
	<%@ include file="/styles/clean.css" %>
	</style>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
	<script>
	<%@ include file="/scripts/node.js" %>
	<%@ include file="/scripts/aleTree.js" %>
	</script>
</head>
<body>
	<div class="header" >
		<span class="logo">
			<object data="/images/ale_tree.svg" type="image/svg+xml">
				<img src="/images/ale_tree.png"/>
			</object>
		</span>
		<div class="text">
			<h1>The Ale Tree</h1>
			<p>
				The Ale Tree is an <strong>Anonymous Link Exchange</strong>.<br/> 
				You can post links, vote and comment without logging in. <a href="/docs/howaletreeworks.html">Learn more...</a>
			</p>
		</div>
	</div>