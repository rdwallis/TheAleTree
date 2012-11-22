package com.wallissoftware.ale.tags;

import java.io.IOException;
import java.net.URL;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class HostStripTag extends SimpleTagSupport {
	
	private String url;
	
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		URL u = new URL(url);
		out.print(u.getHost());
		
	}
	
	

}
