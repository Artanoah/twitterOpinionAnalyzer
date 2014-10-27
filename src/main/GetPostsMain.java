package main;

import net.dean.jraw.http.NetworkException;
import contentSource.RedditPosts;

public class GetPostsMain {

	public static void main(String[] args) {
		try {
			RedditPosts.getTrainingsSet("./reddit.txt", "2kdpuj");
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
