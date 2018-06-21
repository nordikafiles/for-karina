import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import json.JSONObject;
import json.JSONArray;

// import App;
public class Main {
    public static final String VK_ENDPOINT = "https://api.vk.com/method/";
    public static final String VK_ACCESS_TOKEN = "40383436280e1d91d55f82cde53186d3fdfcf64d06751cbfc34cd68b197aa7e5ed13c283a48df2b1f4c21";
    public static final String VK_VERSION = "5.67";

    public static JSONObject LONG_POLL_CONFIG;

    public static void alal() {
    }

    static JSONObject httpRequest(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
        
        return new JSONObject(response.toString());
    }

    static JSONObject getLongPollingServer() throws Exception {
        LONG_POLL_CONFIG = (JSONObject) httpRequest(VK_ENDPOINT + "messages.getLongPollServer?access_token=" + VK_ACCESS_TOKEN + "&v=" + VK_VERSION).get("response");
        return LONG_POLL_CONFIG;
    }

    static void longPoll() throws Exception {
        getLongPollingServer();
        while(true) {
            JSONObject result = httpRequest("https://" + LONG_POLL_CONFIG.get("server") + "?act=a_check&key=" + LONG_POLL_CONFIG.get("key") + "&ts=" + LONG_POLL_CONFIG.get("ts") + "&wait=25");
            LONG_POLL_CONFIG.put("ts", result.get("ts"));
            JSONArray updates = (JSONArray) result.get("updates");
            for (int i = 0; i < updates.length(); i++) {
                JSONArray update = (JSONArray) updates.get(i);
                if ((int) update.get(0) == 4 && (int) update.get(2) != 3) {
                    String message = (String) update.get(6);
                    String user = (String) update.get(3).toString();
                    sendMessage(user, "Все говорят \"" + message + "\", а ты купи слона!");
                }
            }
        }
    }

    static void sendMessage(String user, String message) throws Exception {
        message = EncodingUtil.encodeURIComponent(message);
        System.out.println(message);
        httpRequest(VK_ENDPOINT + "messages.send?access_token=" + VK_ACCESS_TOKEN + "&v=" + VK_VERSION + "&peer_id=" + user + "&message=" + message);
    }

    public static void main(String[] args) throws Exception {
        // httpRequest("https://google.com");
        longPoll();
    }
    
}