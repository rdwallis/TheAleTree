package com.wallissoftware.ale.config;

import com.google.inject.AbstractModule;
import com.googlecode.htmleasy.HtmleasyProviders;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.ale.model.Node;
import com.wallissoftware.ale.model.Rss;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;
import com.wallissoftware.ale.resource.AdminResource;
import com.wallissoftware.ale.resource.NodeResource;
import com.wallissoftware.ale.resource.RssResource;
import com.wallissoftware.ale.resource.TeamResource;

public class ConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		ObjectifyService.register(Node.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Team.class);
		ObjectifyService.register(Rss.class);
		for (Class<?> clazz: HtmleasyProviders.getClasses()) {
			bind(clazz);
		}
		bind(NodeResource.class);
		bind(TeamResource.class);
		bind(AdminResource.class);
		bind(RssResource.class);
		
		
	}

}
