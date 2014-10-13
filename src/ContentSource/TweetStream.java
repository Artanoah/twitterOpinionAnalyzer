package ContentSource;

import java.io.IOException;
import java.util.ArrayList;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TweetStream {
	private TweetStream(){
	}
	/**Starts listening on the TwitterStream. And filter it by some parameters.
	 * @param maxTweets amount of Tweets which shall be saved. They'll be returned.
	 * @return List with TextContent of the streamed Tweets
	 */
	public static ArrayList<String> getTweetsFromStream(final int maxTweets){
		
		final Object lock = new Object();
		final ArrayList<String> tweetContents = new ArrayList<String>();
		
		// #### TWEETSTREAM STARTEN ####
		StatusListener listener = new StatusListener(){
			
	        public void onStatus(Status status) {
	        	if(tweetContents.size() < maxTweets){
	        		tweetContents.add(status.getText());
	        	} else {
	        		TwitterStreamFactory.getSingleton().cleanUp();
	        		synchronized(lock){lock.notify();}
	        	}
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			public void onScrubGeo(long arg0, long arg1) {}
			public void onStallWarning(StallWarning arg0) {}
		};
		
		// #### OAUTH KEYS ZUSAMMENBAUEN ####
		String consumer_key = "DJ3Lw8rFQ0aRReI715rTuQ";
		String consumer_secret = "mz6FbvN0H1uYawH4AjsPNcUyWweMoEIMwjQhgYqSY";
		String access_token_key = "913144033-MAgGE9fsFlFOfdFY2i6UwdurPUj19yZZYyXfH7IU";
		String access_token_secret = "qQbVKRK2XF8W7JZN42YISNj7fzjyrru5aXu1LWDhvfU";
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.setOAuthAccessToken(access_token_key);
		config.setOAuthAccessTokenSecret(access_token_secret);
		config.setOAuthConsumerKey(consumer_key);
		config.setOAuthConsumerSecret(consumer_secret);
		Configuration configuration = config.build();
		TwitterStreamFactory configured_Stream = new TwitterStreamFactory(configuration);
		
		// ##### FILTER ZUSAMMENBAUEN ####
		FilterQuery filter = new FilterQuery();
		filter.language(new String[]{"en"});
		String[] keyWords = {"Obama"};
		filter.track(keyWords);
		
		
		// #### TWITTERSTREAM KONFIGURIEREN ####
		@SuppressWarnings("static-access")
		TwitterStream twitterStream = configured_Stream.getSingleton();
	    twitterStream.addListener(listener);
	    twitterStream.filter(filter);
	    synchronized(lock){try {
			lock.wait();
		} catch (InterruptedException e) {
			System.out.println("ERROR: Beim Warten auf das Notify zum Weitergehen!");
		}}
		return tweetContents;
	}
}
