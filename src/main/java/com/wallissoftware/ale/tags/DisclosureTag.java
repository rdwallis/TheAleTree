package com.wallissoftware.ale.tags;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DisclosureTag extends SimpleTagSupport {
	
	private String href;
	private String text;
	private boolean open = false;
	
	private final StringWriter sw = new StringWriter();

	public void doTag() throws JspException, IOException {
		
		JspWriter out = getJspContext().getOut();
		out.print("<div class=\"disclosurePanel disclosurePanel-");
		out.print(isOpen() ? "open" : "closed");
		out.print("\"><a href=\"");
		out.print(getHref());
		out.print("\" class=\"disclosurePanel-header\" onclick=\"return aleTree.toggleDisclosure($(this), '");
		out.print(getStripHref());
		out.print("')\"> <svg	xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 50 50\">"+
		"<line x1=\"25\" y1=\"0\" x2=\"25\" y2=\"45\" class=\"closed\" />" +
				"<line x1=\"0\" y1=\"23\" x2=\"50\" y2=\"23\" class=\"closed\" />"+
		"<line x1=\"10\" y1=\"23\" x2=\"40\" y2=\"23\" class=\"open\" />" +
				"</svg>");
		out.print(getText());
		out.print("</a><div class=\"disclosurePanel-content\">");
		if (getJspBody() != null) {
			getJspBody().invoke(sw);
			final String content = sw.toString();
			out.print(content);
		} 
		out.print("</div></div>");
	}
	
	private String getStripHref() {
		if (getHref().contains("?")) {
			return getHref() + "&strip=true";
		} else {
			return getHref() + "?strip=true";
		}
	}

}
