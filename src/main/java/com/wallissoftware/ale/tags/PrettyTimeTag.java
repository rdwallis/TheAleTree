package com.wallissoftware.ale.tags;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.ocpsoft.pretty.time.PrettyTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PrettyTimeTag extends SimpleTagSupport {
	
	private long date;
	
	private final PrettyTime pt = new PrettyTime();

	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		out.print(pt.format(new Date(getDate())));
		
	}
	
	

}
