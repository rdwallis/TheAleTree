package com.wallissoftware.ale.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lombok.extern.java.Log;

import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.inject.Inject;
import com.wallissoftware.ale.model.ActionType;
import com.wallissoftware.ale.model.Node;
import com.wallissoftware.ale.model.Rss;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;
import com.wallissoftware.ale.model.Vote;

@Log
public class RssDao {

	private final NodeDao nodeDao;
	private final TeamDao teamDao;
	private final UserDao userDao;

	@Inject
	public RssDao(final NodeDao nodeDao, final TeamDao teamDao,
			final UserDao userDao) {
		this.nodeDao = nodeDao;
		this.teamDao = teamDao;
		this.userDao = userDao;
	}

	public Rss get(final String url) {
		return ofy().load().type(Rss.class).id(url).get();
	}

	public Rss create(final String url, final String user) {
		Rss rss = get(url);
		if (rss == null) {
			rss = new Rss();
			rss.setUrl(url);
		}
		rss.getUsers().add(user);
		ofy().save().entities(rss);
		return rss;
	}

	public void updateAll() {
		Random random = new Random();
		final RssParser parser = new RssParser();
		final QueryResultIterator<Rss> it = ofy().load().type(Rss.class)
				.iterator();
		final Set<Rss> toSave = new HashSet<Rss>();
		while (it.hasNext()) {
			try {
				Rss rss = it.next();
				toSave.add(rss);
				rss.setLastUpdated(System.currentTimeMillis());
				List<RssItemBean> items = parser.load(rss.getUrl()).getItems();
				RssItemBean item = items.get(random.nextInt(Math.min(5,
						items.size())));
				Date created;
				try {
					created = item.getPubDate();
					if (System.currentTimeMillis() - created.getTime() > 86400000) {
						break;
					}
				} catch (Exception e) {
					created = new Date();
				}
				String url = item.getLink();
				URL u = new URL(url);
				if (u.getHost().contains("news.google.com")) {
					String[] params = u.getQuery().split("&");
					Map<String, String> map = new HashMap<String, String>();
					for (String param : params) {
						String name = param.split("=")[0];
						String value = param.split("=")[1];
						map.put(name.toLowerCase(), value);
					}
					if (map.containsKey("url")) {
						url = map.get("url");
					}
				}

				Node node = null;
				for (String ip : rss.getUsers()) {
					log.info("From Ip: " + ip);
					final User user = userDao.get(ip);
					Team team = teamDao.get(user.getTeam());
					if (team == null) {
						team = userDao.assignTeam(user);
					}
					log.info("Team Assigned: " + team.getId());
					if (node == null) {
						node = nodeDao.create(url, null, 0, user, team, false,
								created);
						log.info("Node Created");
					} else {
						nodeDao.doAction(node, user, team, ActionType.VOTE,
								Vote.UP, false);
					}

				}
			} catch (Exception e) {
				log.warning(e.getMessage());
			}
		}
		ofy().save().entities(toSave);
	}

}
