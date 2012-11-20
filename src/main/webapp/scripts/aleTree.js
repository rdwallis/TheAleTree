var aleTree = {
	
	toggleDisclosure: function(elem, href) {
		elem.parent().toggleClass("disclosurePanel-open disclosurePanel-closed");
		if (!elem.parent().find(".disclosurePanel-content").html()) {
			$.ajax({
				type: "GET",
				url: href,
				success: function (data) {
					elem.parent().find(".disclosurePanel-content").html(data);
				},
			});
		}
		return false;
	},
	
		
};