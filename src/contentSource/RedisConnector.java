package contentSource;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

import main.Vector;
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
	
	public static void writeVectorToRedis(Vector data){
		if(redisServer == null){
			connectToServer();
		}
		//JSON-Object zusammenstecken
		JSONObject json = new JSONObject();
		json.put("data", data.getMap());
		json.put("class", data.getValue());
		
		//JSON-Object zu Redis schicken
		redisServer.set(String.valueOf(uniqueKey), json.toString());
		
		//Wert von maxKey persistent erhoehen
		redisServer.set("maxKey", String.valueOf(uniqueKey));
		uniqueKey +=1;
	}

	public static Vector getVectorFromRedis(String key){
		if(redisServer == null){
			connectToServer();
		}
		String jsonString = redisServer.get(key);
		JSONObject jsonObject = new JSONObject(jsonString);
		HashMap<String,Integer> accu  = new HashMap<String,Integer>();
		for(Object jo : jsonObject.getJSONObject("data").keySet()){
			accu.put(jo.toString(), jsonObject.getJSONObject("data").getInt(jo.toString()));
		}
		
		
		return new Vector(accu , (int)jsonObject.get("class"));
	}
	
}
