package contentSource;
import java.util.List;

import redis.clients.jedis.*;

public class RedisConnector {
	static Jedis redisServer = null;
	static int uniqueKey = 1;
	
	private RedisConnector(){}
	private static void connectToServer(){
		redisServer = new Jedis("localhost");
		redisServer.connect();
		uniqueKey = Integer.valueOf(redisServer.get("maxKey"));
	}
	
	public static void writeVectorToRedis(String data){
		if(redisServer == null){
			connectToServer();
		}
		redisServer.set(String.valueOf(uniqueKey), data);
		redisServer.set("maxKey", String.valueOf(uniqueKey));
		uniqueKey +=1;
	}
	public static String getVectorFromRedis(String key){
		if(redisServer == null){
			connectToServer();
		}
		return redisServer.get(key);
	}
	
}
