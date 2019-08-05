package com.awaymeet.dl4j.test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class ShuangSeQiuLotteryCrawler {
    public static void main(String[] args) {
    	String sendGet = sendGet();
		System.out.println(sendGet);
		/*JSONObject jsonObject = JSONObject.fromObject(sendGet);
        JSONArray loopJson1 = jsonObject.getJSONArray("result");*/
		JSONArray loopJson1 = loopJson(sendGet, "result");
		System.out.println("loopJson1.size()"+loopJson1.size());
		String fly="{";
        for(int i=0;i<loopJson1.size();i++){
        	String resOne = loopJson1.get(i).toString();
        	/*Item item = JsonUtils.jsonToPojo(resOne, Item.class);
        	System.out.println(item.getCode()+"___"+item.getRed()+"___"+item.getBlue());*/
        	JSONObject resOneOb = JSONObject.fromObject(resOne);
        	System.out.println(resOneOb.get("code").toString()+"___"+resOneOb.get("red").toString()+"___"+resOneOb.get("blue").toString());
        	String[] split = resOneOb.get("red").toString().split(",");
        	String red="";
        	for(int j=0;j<split.length;j++){
        		String s=split[j];
        		if(s.substring(0,1).equals("0")){
        			s=s.substring(1,s.length());
        		}
        		red+=s+",";
        	}
        	String b = resOneOb.get("blue").toString();
        	if(b.substring(0,1).equals("0")){
    			b=b.substring(1,b.length());
    		}
        	fly+="{"+red+b+"},";
        }
        fly+="}";
        System.out.println(fly);
	}
    public static JSONArray loopJson(String json,String key){
    	JSONObject jsonObject = JSONObject.fromObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray(key);
        return jsonArray;
    }
	public static  String sendGet() {
        String result = "";
        String url="http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=2013-01-01&dayEnd=2019-08-02&pageNo=7";
        try {
			URL realUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Cookie", "UniqueID=begfnIQk9w9la5YK1564669663485; Sites=_21; _ga=GA1.3.1578862439.1564669664; _gid=GA1.3.1539255600.1564669664; 21_vq=2");
			connection.setRequestProperty("Host", "www.cwl.gov.cn");
			connection.setRequestProperty("Referer", "http://www.cwl.gov.cn/kjxx/ssq/kjgg/");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			connection.setRequestMethod("GET");
			connection.connect();
			
			InputStream urlStream = new GZIPInputStream(connection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream,"UTF-8"));
			String line;
			while((line = reader.readLine()) != null) {
				result+=line;
			}
			reader.close();
			urlStream.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
    }
}
