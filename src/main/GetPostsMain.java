package main;

import java.io.IOException;

import net.dean.jraw.http.NetworkException;
import contentSource.RedditPosts;

public class GetPostsMain {

	public static void main(String[] args) throws NetworkException, IOException {
		RedditPosts.getTrainingsSetToFile("./reddit.txt", "2kdpuj");
		
		RedditPosts.pushTrainingsSetToRedis("./reddit.txt");
	}

}
