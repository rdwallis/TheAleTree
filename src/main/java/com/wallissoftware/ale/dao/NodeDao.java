package com.wallissoftware.ale.dao;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.java.Log;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.Ref;
import com.wallissoftware.ale.exceptions.InvalidHeirachyException;
import com.wallissoftware.ale.exceptions.InvalidNodeException;
import com.wallissoftware.ale.model.ActionType;
import com.wallissoftware.ale.model.Node;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;
import com.wallissoftware.ale.model.Vote;

@Log
public class NodeDao {

	public Node get(final long id) {
		if (id == 0) {
			return null;
		}
		Node node = ofy().load().type(Node.class).id(id).get();
		if (node.isRedirect()) {
			node = ofy().load().type(Node.class).id(node.getRedirectId()).get();
		}
		return node;
	}

	public Node create(final String url, final String comment,
			final long parentId, final User user, final Team team,
			final boolean ignore) throws InvalidNodeException,
			InvalidHeirachyException {
		return create(url, comment, parentId, user, team, ignore, null);
	}

	public Node create(final String url, final String comment,
			final long parentId, final User user, final Team team,
			final boolean ignore, final Date created)
			throws InvalidNodeException, InvalidHeirachyException {
		log.info("Creating Node: " + url);
		Node node = new Node(url, comment, parentId,
				created == null ? System.currentTimeMillis()
						: created.getTime());
		Node existing = null;
		log.info("Check Exists");
		if (node.getUrl() != null) {
			existing = getByUrl(node.getUrl());
			if (existing != null) {
				if (!existing.getParents().contains(parentId)) {
					return doAction(existing, user, team, ActionType.LINK,
							Vote.UP, get(parentId), ignore);
				} else {
					node = existing;
					node.getParents().add(parentId);
				}
			}

		}
		log.info("Process parent");
		Node parent = null;
		if (parentId != 0) {
			parent = ofy().load().type(Node.class).id(parentId).get();
			if (parent == null) {
				throw new InvalidHeirachyException();
			}
		}
		log.info("Instant Save");
		ofy().save().entity(node).now();
		if (parent != null) {
			parent.incChildCount();
			ofy().save().entity(parent);
		}
		log.info("Add to taskqueue");
		if (existing == null) {
			Queue queue = QueueFactory.getDefaultQueue();
			try {
				queue.add(withUrl("/1/admin/normalizenode/" + node.getId()
						+ "/" + URLEncoder.encode(user.getIpAddress(), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				log.warning("Failed to add Node to Queue: " + node.getId());
			}
		}

		return doAction(node, user, team, ActionType.VOTE, Vote.UP, ignore);
	}

	public void normalizeNode(final Node node, final User user, final Team team)
			throws InvalidHeirachyException {
		boolean modified = false;
		try {
			URL url = new URL(node.getUrl());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));

			String line;
			StringBuffer html = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				html.append(line);
			}
			reader.close();

			Pattern canonicalRgx = Pattern.compile(
					"<\\s*link\\s*[^>]*rel\\s*=\\s*\"canonical\"[^>]*>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

			Matcher canonicalMatch = canonicalRgx.matcher(html);
			if (canonicalMatch.find()) {
				Pattern hrefRgx = Pattern.compile("href\\s*=\\s*\"[^\"]*\"",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher hrefMatch = hrefRgx.matcher(canonicalMatch.group());
				if (hrefMatch.find()) {
					Pattern urlRgx = Pattern.compile("\"[^\"]*",
							Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
					Matcher urlMatch = urlRgx.matcher(hrefMatch.group());
					if (urlMatch.find()) {
						final String canUrl = urlMatch.group().substring(1);
						try {
							new URL(canUrl);
							if (!canUrl.equals(node.getUrl())) {
								Node existing = getByUrl(canUrl);

								if (existing != null) {
									node.setRedirectId(existing.getId());
									long parentId = node.getParents()
											.iterator().next();
									if (!existing.getParents().contains(
											parentId)) {
										doAction(existing, user, team,
												ActionType.LINK, Vote.UP,
												get(parentId), false);
									}
									ofy().save().entities(node);
									return;
								}
								node.setUrl(canUrl);
								modified = true;
							}
						} catch (MalformedURLException e) {
							// Passthrough
						}
					}
				}
			}
			Pattern headlineRgx = Pattern.compile(
					"<\\s*meta[^>]*itemprop\\s*=\\s*\"headline\"[^>]*>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

			Matcher headlineMatch = headlineRgx.matcher(html);
			String titleMatch = null;
			if (headlineMatch.find()) {
				titleMatch = headlineMatch.group();
			} else {
				Pattern openGraphTitleRgx = Pattern.compile(
						"<\\s*meta[^>]*property\\s*=\\s*\"og:title\"[^>]*>",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

				Matcher ogTMatch = openGraphTitleRgx.matcher(html);
				if (ogTMatch.find()) {
					titleMatch = ogTMatch.group();
				}
			}
			if (titleMatch != null) {
				Pattern contentRgx = Pattern.compile(
						"content\\s*=\\s*\"[^\"]*\"", Pattern.CASE_INSENSITIVE
								| Pattern.MULTILINE);
				Matcher contentMatch = contentRgx.matcher(titleMatch);
				if (contentMatch.find()) {
					Pattern contentStringRgx = Pattern.compile("\"[^\"]*",
							Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
					Matcher contentStringMatch = contentStringRgx.matcher(contentMatch.group());
					if (contentStringMatch.find()) {
						final String title = contentStringMatch.group().substring(1);
						node.setTitle(title);
						modified = true;
					}
				}
			} else {

				Pattern titleRgx = Pattern.compile(
						"<\\s*(title)\\s*>[^<]*<\\s*/",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

				Matcher matcher = titleRgx.matcher(html);
				if (matcher.find()) {
					Pattern innerRgx = Pattern.compile(">[^<]*",
							Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
					Matcher inner = innerRgx.matcher(matcher.group());
					inner.find();
					node.setTitle(inner.group().substring(1));
					modified = true;
				}
			}

		} catch (MalformedURLException e) {

		} catch (IOException e) {
			// ...
		} catch (Exception e) {

		}
		if (modified) {
			ofy().save().entity(node);
		}

	}

	private Node getByUrl(final String url) {
		Ref<Node> ref = ofy().load().type(Node.class).filter("url =", url)
				.limit(1).first();
		if (ref == null) {
			return null;
		} else {
			return ref.get();
		}
	}

	public Node doAction(Node node, final User user, final Team team,
			final ActionType actionType, final Vote vote, final boolean ignore) {
		try {
			return doAction(node, user, team, actionType, vote, null, ignore);
		} catch (InvalidHeirachyException e) {
			// this wont happen
			throw new RuntimeException();
		}
	}

	public Node doAction(Node node, final User user, final Team team,
			final ActionType actionType, final Vote vote, final Node parent,
			final boolean ignore) throws InvalidHeirachyException {
		if ((!ignore) && (team.doAction(node, user, actionType, vote, parent))) {
			ofy().save().entity(node);
			if (parent != null) {
				ofy().save().entity(parent);
			}
		}
		ofy().save().entity(team);
		ofy().save().entity(user);
		if (actionType == ActionType.VOTE) {
			ofy().save().entity(node);
		}
		return node;
	}

	public void updateNodeRanks() {
		QueryResultIterator<Node> iter = ofy().load().type(Node.class)
				.filter("nextCalculationTime < ", System.currentTimeMillis())
				.iterator();

		final Set<Node> toSave = new HashSet<Node>();
		while (iter.hasNext()) {
			final Node node = iter.next();
			toSave.add(node);
			node.updateWeightedWilsonScore();
		}

		ofy().save().entities(toSave);
	}

	public Collection<Node> getChildren(long id, int offset, int limit) {
		return ofy().load().type(Node.class).filter("parents", id)
				.filter("redirect", false).order("-weightedWilsonScore")
				.order("-reset").offset(offset).limit(limit).list();
	}

	public void normalizeAll(User user, Team team)
			throws InvalidHeirachyException {
		QueryResultIterator<Node> iter = ofy().load().type(Node.class)
				.iterator();
		while (iter.hasNext()) {
			Node node = iter.next();
			if (node.getUrl() != null) {
				normalizeNode(node, user, team);
			}
		}

	}

}
