/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.showcase.facebook;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.FqlResult;
import org.springframework.social.facebook.api.FqlResultMapper;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.support.URIBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FacebookRestController {

	private static final String GRAPH_API_URL = "https://graph.facebook.com/";

	private final Facebook facebook;

	@Inject
	public FacebookRestController(Facebook facebook) {
		this.facebook = facebook;
	}

	@RequestMapping(value = "/facebook/insight/json", method = RequestMethod.GET)
	public @ResponseBody Page showMyPage(Model model) {

		URI uri = URIBuilder.fromUri(
				GRAPH_API_URL + "testinsightapi/insights?fields=id,name")
				.build();
		ResponseEntity<Page> page1 = facebook.restOperations().getForEntity(
				uri, Page.class);
		Page body = page1.getBody();

		return body;
	}

	@RequestMapping(value = "/facebook/feed/json", method = RequestMethod.GET)
	public @ResponseBody Page showFeedJson(Model model) {

		URI uri = URIBuilder.fromUri(GRAPH_API_URL + "cocacolaIndia/feed")
				.build();
		ResponseEntity<Page> page1 = facebook.restOperations().getForEntity(
				uri, Page.class);

		Page body = page1.getBody();

		return body;
	}

	@RequestMapping(value = "/facebook/feed/users/json", method = RequestMethod.GET)
	public @ResponseBody List<FacebookProfile> showFeed(Model model) {
		FacebookProfile profile = facebook.userOperations().getUserProfile(
				"cocacolaIndia");

		Page page = facebook.pageOperations().getPage("cocacolaIndia");
		PagedList<Post> list = facebook.feedOperations().getFeed(
				"cocacolaIndia");
		PagingParameters nextPage = list.getNextPage();
		nextPage = new PagingParameters(250, nextPage.getOffset(),
				nextPage.getSince(), nextPage.getUntil());
		PagedList<Post> list2 = facebook.feedOperations().getFeed(
				"cocacolaIndia", nextPage);

		List<FacebookProfile> userProfiles = new ArrayList<FacebookProfile>();

		
		// Getting friends-of-others not supported like this....
		Iterator<Post> iterator = list2.iterator();
		while (iterator.hasNext()) {
			Post next = iterator.next();
			Reference user = next.getFrom();
			userProfiles.add(facebook.userOperations().getUserProfile(
					user.getId()));
			List<Comment> comments = next.getComments();
			if (comments != null) {
				for (Comment comment : comments) {
					Reference from = comment.getFrom();
					userProfiles.add(facebook.userOperations().getUserProfile(
							from.getId()));
					List<Reference> likes = comment.getLikes();
					if (likes != null) {
						for (Reference ref : likes) {
							userProfiles.add(facebook.userOperations()
									.getUserProfile(ref.getId()));
						}
					}
				}
			}
			List<Reference> postLikes = next.getLikes();
			if (postLikes != null) {
				for (Reference postLikesRef : postLikes) {
					userProfiles.add(facebook.userOperations().getUserProfile(
							postLikesRef.getId()));
				}
			}

		}

		return userProfiles;
	}

}
