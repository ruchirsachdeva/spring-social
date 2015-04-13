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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
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
public class FacebookFeedController {

	private static final String GRAPH_API_URL = "https://graph.facebook.com/";

	private final Facebook facebook;

	@Inject
	public FacebookFeedController(Facebook facebook) {
		this.facebook = facebook;
	}
	
	@RequestMapping(value="/facebook/feed", method=RequestMethod.GET)
	public String showFeed(Model model) {
		 FacebookProfile profile = facebook.userOperations().getUserProfile("cocacolaIndia");

		    Page page = facebook.pageOperations().getPage("cocacolaIndia");
		    PagedList<Post> list= facebook.feedOperations().getFeed("cocacolaIndia");
		    PagingParameters nextPage = list.getNextPage();
		    nextPage = new PagingParameters(250, nextPage.getOffset(), nextPage.getSince(), nextPage.getUntil());
		    PagedList<Post> list2= facebook.feedOperations().getFeed("cocacolaIndia",nextPage);
		 

		    
		    //    (#12) fql is deprecated for versions v2.1 and higher
		  /*  
		    String query = "SELECT uid, name, work_history FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 IN (SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = "+list2.get(0).getFrom().getId()+" ) and is_app_user=1) )";
	    	 FqlResultMapper<String> mapper = new FqlResultMapper<String>() {
				
				@Override
				public String mapObject(FqlResult objectValues) {
					return objectValues.getString("name");
				}
			};
			List<String> query2 = facebook.fqlOperations().query(query, mapper);
			String string = query2.get(0);
			*/
			
		    
		    // Getting friends-of-others not supported like this....
		   /* Iterator<Post> iterator = list2.iterator();
		    while(iterator.hasNext()) {
		    	Post next = iterator.next();
		    	String id = next.getFrom().getId();
		    	 PagedList<Reference> fbProf = facebook.friendOperations().getFriendLists(id);
		    	 
		    	
		    	 Iterator<Reference> frndItr = fbProf.iterator();
		    	 while(frndItr.hasNext()) {
		    		 Reference next2 = frndItr.next();
		    		 next2.getName();
		    		 FacebookProfile userProfile = facebook.userOperations().getUserProfile(next2.getId());
		    		 String about = userProfile.getAbout();
		    		 String name = userProfile.getName();
		    		 
		    	 }
		    }*/
		    
		    
			model.addAttribute("feed", list2);
			
	//	model.addAttribute("feed", facebook.feedOperations().getFeed());
		return "facebook/feed";
	}
	
	@RequestMapping(value="/facebook/feed/json", method=RequestMethod.POST)
	public String postUpdate(String message) {
		facebook.feedOperations().updateStatus(message);
		return "redirect:/facebook/feed";
	}
	
	
	
	
	
}
