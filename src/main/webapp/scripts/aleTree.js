var aleTree = {
	
	toggleDisclosure: function(elem, href) {
		elem = $(elem);
		elem.parent().toggleClass("disclosurePanel-open disclosurePanel-closed");
		if (!elem.parent().find(".disclosurePanel-content").html()) {
			elem.parent().find(".disclosurePanel-content").html("<img src='/images/load.gif' class='load'/>");
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