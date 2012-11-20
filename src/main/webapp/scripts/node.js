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
	
	toggleChildren: function(id) {
		$("#disclosurePanel" + id).toggleClass("disclosurePanel-open disclosurePanel-closed");
		if (!$("#childPanel" + id).html().trim()) {
			node.refreshChildren(id);
		}
		
	},
	
	refreshChildren: function(id) {
		$.ajax({
			type: "GET",
			url: "/1/node/" + id + "?strip=true",
			success: function (data) {
				$("#childPanel" + id).html(data);
			},
		});
	},
	
	toggleAttach: function(id) {
		$("#attachPanel" + id).toggleClass("disclosurePanel-open disclosurePanel-closed");
	}
	
};