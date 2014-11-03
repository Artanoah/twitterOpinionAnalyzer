package contentSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.*;

import main.FeatureVector;
import redis.clients.jedis.*;

public class RedisConnector {
	static Jedis redisServer = null;
	static int uniqueKey = 1;
	static String keyText = "text";
	static String keyBewertung = "bewertung";
	
	private RedisConnector(){}
	private static void connectToServer(){
		redisServer = new Jedis("localhost");
		redisServer.connect();
		uniqueKey = Integer.valueOf(redisServer.get("maxKey"));
	}
	
	/**Schreibt einen Feature-Vektor in die Redis-Datenbank
	 * @param data zu schreibender Vektor
	 */
	public static void writeVectorToRedis(FeatureVector data){
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
	
	/**Schreibt einen Post in die Redis-Datenbank.
	 * @param post Der zu schreibende Post, im Klartext
	 * @param bewertung Die zu schreibende Bewertung als int
	 */
	public static void writePostToRedis(String post,int bewertung){
		if(redisServer == null){
			connectToServer();
		}
		//JSON-Object erstellen
		JSONObject json = new JSONObject();
		json.put(keyText,post);
		json.put(keyBewertung, bewertung);
		
		//JSON-Object zu Redis schicken
		redisServer.set(String.valueOf(uniqueKey), json.toString());
		
		//Wert von maxKey persistent erhoehen
		redisServer.set("maxKey", String.valueOf(uniqueKey));
		uniqueKey +=1;
	}

	/**Holt einen bestimmten Feature-Vektor von Redis
	 * @param key Key von einem bestimmten Vektor
	 * @return Vektor, der hinter dem Key steckt
	 */
	public static FeatureVector getVectorFromRedis(String key){
		if(redisServer == null){
			connectToServer();
		}
		String jsonString = redisServer.get(key);
		JSONObject jsonObject = new JSONObject(jsonString);
		HashMap<String,Integer> accu  = new HashMap<String,Integer>();
		for(Object jo : jsonObject.getJSONObject("data").keySet()){
			accu.put(jo.toString(), jsonObject.getJSONObject("data").getInt(jo.toString()));
		}
		
		
		return new FeatureVector(accu , (int)jsonObject.get("class"));
	}
	
	/**Holt einen Post von Redis, inkl. dessen Bewertung.
	 * @param key Key von einem bestimmten Post
	 * @return HashMap mit zwei Keys "text" und "bewertung"
	 */
	public static Map<String,Integer> getPostsFromRedis(){
		Map<String,Integer> result = new HashMap<String,Integer>();
		if(redisServer == null){
			connectToServer();
		}
		Set<String> redisKeys = redisServer.keys("*");
		for(String key : redisKeys){
			JSONObject jsonObject = new JSONObject(redisServer.get(key));
			result.put(jsonObject.getString(keyText), jsonObject.getInt(keyBewertung));
		}
		
		return result;
	}
	
}
