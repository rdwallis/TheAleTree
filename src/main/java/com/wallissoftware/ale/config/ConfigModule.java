package com.wallissoftware.ale.config;

import com.google.inject.AbstractModule;
import com.googlecode.htmleasy.HtmleasyProviders;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.ale.api.AdminApi;
import com.wallissoftware.ale.api.NodeApi;
import com.wallissoftware.ale.api.RssApi;
import com.wallissoftware.ale.api.TeamApi;
import com.wallissoftware.ale.model.Node;
import com.wallissoftware.ale.model.Rss;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;

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
		bind(NodeApi.class);
		bind(TeamApi.class);
		bind(AdminApi.class);
		bind(RssApi.class);
		
		
	}

}
