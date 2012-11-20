var node = {
	vote: function (id, up) {
		var vCount = $("#vCount" + id);
		var count = parseInt(vCount.text());
		var upElem = $("#up" + id);
		var downElem = $("#down" + id);
		var doAction = true;
		if (upElem.attr("class") === "selected") {
			upElem.attr("class", "unselected");
			count--;
			doAction = !up;
		}
		if (downElem.attr("class") === "selected") {
			downElem.attr("class", "unselected");
			count++;
			doAction = doAction && up;
		}
		if (doAction) {
			if (up) {
				upElem.attr("class", "selected");
			} else {
				downElem.attr("class", "selected");
			}
			$.ajax({
				type: "GET",
				url: "/1/node/" + id +  (up ? "/up" : "/down"),
				success: function () {
					
				},
			});
			count += up ? 1 : -1;
		}
		vCount.text(count);
	},
	
};