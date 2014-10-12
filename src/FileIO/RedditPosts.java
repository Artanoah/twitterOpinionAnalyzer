package FileIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.SubredditPaginator;


public class RedditPosts {
	
	private RedditPosts(){}
	
	static public List<List> getRedditPosts(String subreddit,int amount){
		RedditClient reddit = new RedditClient("bot/1.0 by name");
		List<List> result = new ArrayList<List>();
		
		SubredditPaginator frontPage = new SubredditPaginator(reddit,subreddit); // Second parameter could be a subreddit
		Listing<Submission> listing = frontPage.next();
	    for (int i = 0 ; i < amount ; i++) {
	    	Submission submission = listing.get(i);
	        result.add(new ArrayList<Object>(Arrays.asList(submission.getTitle(),submission.getSelftext().md(),submission.getComments())));
	    }
		return result;
	}
}
