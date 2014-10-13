package ContentSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.SubredditPaginator;



public class RedditPosts {
	
	private RedditPosts(){}
	
	/**Laedt die ersten amountPosts vom Subreddit subreddit
	 * @param subreddit subreddit von dem geladen werden soll
	 * @param amountPosts Anzahl der zu ladenden Posts
	 * @return Liste bei dem ein Element die folgenden Dinge enthaelt: (String) Titel des Posts, (String) Beschreibung des Posts, (Listing<Comment>) Alle Kommentare zu diesem Post
	 */
	static public List<List> getSubreddit(String subreddit,int amountPosts){
		RedditClient reddit = new RedditClient("bot/1.0 by name");
		List<List> result = new ArrayList<List>();
		
		SubredditPaginator frontPage = new SubredditPaginator(reddit,subreddit); // Second parameter could be a subreddit
		Listing<Submission> listing = frontPage.next();
	    for (int i = 0 ; i < amountPosts ; i++) {
	    	Submission submission = listing.get(i);
	        result.add(new ArrayList<Object>(Arrays.asList(submission.getTitle(),submission.getSelftext().md(),submission.getComments())));
	    }
		return result;
	}
	
	/**Holt einen ganzen Post von Reddit
	 * @param postId Die eindeutige ID des Posts. (Kriegt man aus dem Link, z.B. "2iwes5")
	 * @return Erhaltener Post, in der JRAW eigenen Implementation
	 * @throws NetworkException Falls der Post nicht übertragen werden kann, wegen Netzwerkporbleme.
	 */
	static public Submission getPost(String postId) throws NetworkException{
		RedditClient reddit = new RedditClient("bot/1.0 by name");
		
		return reddit.getSubmission(postId);
	}
}
