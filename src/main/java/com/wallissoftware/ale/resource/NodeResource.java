package com.wallissoftware.ale.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.htmleasy.View;
import com.wallissoftware.ale.dao.NodeDao;
import com.wallissoftware.ale.dao.TeamDao;
import com.wallissoftware.ale.dao.UserDao;
import com.wallissoftware.ale.exceptions.InvalidHeirachyException;
import com.wallissoftware.ale.exceptions.InvalidNodeException;
import com.wallissoftware.ale.model.ActionType;
import com.wallissoftware.ale.model.Node;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;
import com.wallissoftware.ale.model.Vote;

@Singleton
@Path("/node")
public class NodeResource {

	private final NodeDao nodeDao;
	private final UserDao userDao;
	private final TeamDao teamDao;

	@Inject
	private NodeResource(final NodeDao nodeDao, final UserDao userDao, final TeamDao teamDao) {
		this.nodeDao = nodeDao;
		this.userDao = userDao;
		this.teamDao = teamDao;
	}
	
	@GET
	@Path("/{parentId}/attach")
	public View attachView(@PathParam("parentId") final long parentId, @QueryParam("strip") @DefaultValue("false") final boolean strip, @QueryParam("showlink") @DefaultValue("true") final boolean showLink, @QueryParam("showcomment") @DefaultValue("true") final boolean showComment) throws InvalidNodeException, InvalidHeirachyException {
		ImmutableMap<String, ? extends Object> map = ImmutableMap.of("parentId", parentId, "open", !strip, "showComment", showComment, "showLink", showLink);
		if (strip) {
			return new View("/templates/1/node/attach/attach.jsp", map);
		} else {
			return new View("/templates/1/node/attach/attachFull.jsp", map);
		}
	}
	
	@GET
	@Path("/{parentId}/create")
	public View createNodeView(@PathParam("parentId") final long parentId, @QueryParam("url") @DefaultValue("") String url, @QueryParam("comment") @DefaultValue("") String comment, @Context final HttpServletRequest req, @Context final HttpServletResponse resp, @QueryParam("ignore") @DefaultValue("false") final boolean ignore, @QueryParam("strip") @DefaultValue("false") final boolean strip) throws InvalidNodeException, InvalidHeirachyException {
		final Node node = createNode(parentId, url, comment, req, ignore);
		if (strip) {
			return new View("/templates/1/node/node.jsp", node, "node");
		} else {
			return new View("/templates/1/node/redirect.jsp", node, "node");
		}
	}
	
	
	@GET
	@Path("/{parentId}/create")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	public Node createNode(@PathParam("parentId") final long parentId, @QueryParam("url") @DefaultValue("") String url, @QueryParam("comment") @DefaultValue("") String comment, @Context final HttpServletRequest req, @QueryParam("ignore") @DefaultValue("false") final boolean ignore) throws InvalidNodeException, InvalidHeirachyException {
		if (url.isEmpty()) {
			url = null;
		}
		if (comment.isEmpty()) {
			comment = null;
		}
		final User user = userDao.get(req.getRemoteAddr());
		Team team = teamDao.get(user.getTeam());
		if (team == null) {
			team = userDao.assignTeam(user);
		}
		return nodeDao.create(url, comment, parentId, user, team, ignore);
	}
	
	@PUT
	@Path("/{parentId}/create")
	@Produces({"application/json"})
	public Node createNode(final Node node, @PathParam("parentId") final long parentId, @Context final HttpServletRequest req, @QueryParam("ignore") @DefaultValue("false") final boolean ignore) throws InvalidNodeException, InvalidHeirachyException {
		final String url = node.getUrl();
		final String comment = node.getComment();
		return createNode(parentId, url, comment, req, ignore);
	}
	
	@GET
	@Path("/{id}")
	public View getNodeListView(@PathParam("id") final long id, @QueryParam("offset") @DefaultValue("0") final int offset, @QueryParam("offset") @DefaultValue("25") final int limit, @QueryParam("strip") @DefaultValue("false") final boolean strip) {
		if (strip) {
			return new View("/templates/1/node/nodeList.jsp", getNode(id, offset, limit));
		} else {
			return new View("/templates/1/node/nodeListFull.jsp", getNode(id, offset, limit));
		}
	}
	
	@GET
	@Path("/{id}")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	public ImmutableMap<String, ? extends Object> getNode(@PathParam("id") final long id, @QueryParam("offset") @DefaultValue("0") final int offset, @QueryParam("limit") @DefaultValue("25") int limit) {
		if (id == 0) {
			return ImmutableMap.of("children", nodeDao.getChildren(id, offset, limit));
		} else {
			return ImmutableMap.of("root", nodeDao.get(id), "children", nodeDao.getChildren(id, offset, limit));	
		}
	}
	
	@GET
	@Path("/{id}/up")
	public void upVoteNode(@PathParam("id") final long id, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) {
		doAction(id, req, ActionType.VOTE, Vote.UP, ignore);
	}
	
	@GET
	@Path("/{id}/down")
	public void downVoteNode(@PathParam("id") final long id, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) {
		doAction(id, req, ActionType.VOTE, Vote.DOWN, ignore);
	}
	
	@GET
	@Path("/{id}/spam")
	public void spamNode(@PathParam("id") final long id, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) {
		doAction(id, req, ActionType.SPAM, Vote.UP, ignore);
	}
	
	@GET
	@Path("/{id}/notspam")
	public void notSpamNode(@PathParam("id") final long id, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) {
		doAction(id, req, ActionType.SPAM, Vote.DOWN, ignore);
	}
	
	@GET
	@Path("/{parentId}/{id}/link")
	public void linkNode(@PathParam("id") final long id, @PathParam("parentId") final long parentId, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) throws InvalidHeirachyException {
		doAction(id, req, ActionType.LINK, Vote.UP, nodeDao.get(parentId), ignore);
	}
	
	@GET
	@Path("/{parentId}/{id}/unlink")
	public void unlinkNode(@PathParam("id") final long id, @PathParam("parentId") final long parentId, final @Context HttpServletRequest req,  @QueryParam("ignore") @DefaultValue("false") final boolean ignore) throws InvalidHeirachyException {
		doAction(id, req, ActionType.LINK, Vote.DOWN, nodeDao.get(parentId), ignore);
	}
	
	private Node doAction(final long id, final HttpServletRequest req, final ActionType actionType, final Vote vote, final boolean ignore) {
		try {
			return doAction(id, req, actionType, vote, null, ignore);
		} catch (InvalidHeirachyException e) {
			//this will never happen
			throw new RuntimeException();
		}
	}
	
	private Node doAction(final long id, final HttpServletRequest req, final ActionType actionType, final Vote vote, final Node parent, final boolean ignore) throws InvalidHeirachyException {
		Node node = nodeDao.get(id);
		final User user = userDao.get(req.getRemoteAddr());
		Team team = teamDao.get(user.getTeam());
		if (team == null) {
			team = userDao.assignTeam(user);
		}
		return nodeDao.doAction(node, user, team, actionType, vote, parent, ignore);
	}
	

	
}
