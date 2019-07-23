import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * 从网站上爬取开奖数据，后面实现将爬取的数据存入数据库此处省略
 * Crawl the prize data from the website, and then store the crawled data in the database. This is omitted.
 * */
public class LotteryCrawler {
	public static void main(String[] args) {
		init();
	}
	private static void dataAlasyse(String dop){
		String lrs="<table class=\"win_deail\" style=\"margin-top: 0px;\" id=\"k3_tb\" cellpadding=\"0\" cellspacing=\"0\"";
		String lre="</table>";
		String tableRes=subStartAndEnd(dop, lrs, lre);
		List<String> listByStartEnd = getListByStartEnd(tableRes, "<tr class=\"hide_tr\" style=\"display:none;\" id=\"termCode\">", "</tr>");
		for(String t:listByStartEnd){
			String term=subStartAndEnd(t, "<td class=\"td_play_reward\" style=\"height: 28px;\">", "</td> ");
			term=term.trim();
			String termR1=subStartAndEnd(t, "<b id=k3_0>", "</b>");
			String termR2=subStartAndEnd(t, "<b id=k3_1>", "</b>");
			String termR3=subStartAndEnd(t, "<b id=k3_2>", "</b>");
			System.out.println(term+"——————"+termR1+"——————"+termR2+"——————"+termR3);
		}
	}
	private static void init(){
		String firstRes = JHttpTools.doPost("http://www.jxfczx.cn/report/K3_WinMessage.aspx", null);
		Map<String, String> nextParam=getHiddenPara(firstRes);
		List<String> termList = getDayList();
		for(String t:termList){
			nextParam.put("ddlWinTermCode", t);
			String doPost = JHttpTools.doPost("http://www.jxfczx.cn/report/K3_WinMessage.aspx", nextParam);
			nextParam=getHiddenPara(doPost);
			dataAlasyse(doPost);
		}
		
	}
	private static Map<String, String> getHiddenPara(String res){
		List<String> inputList = getListByStartEnd(res,"<input","/>");
		Map<String, String> nextParam=new HashMap<>();
		nextParam.put("__EVENTTARGET", "ddlWinTermCode");
		nextParam.put("__EVENTARGUMENT", "");
		nextParam.put("__LASTFOCUS", "");
		for(String input:inputList){
			String beginString="name=\"";
			String endString="\"";
			String key=subStartAndEnd(input,beginString,endString);
			beginString="value=\"";
			endString="\"";
			String value=subStartAndEnd(input,beginString,endString);
			nextParam.put(key, value);
		}
		return nextParam;
	}
	private static List<String> getDayList(){
		String res = JHttpTools.doPost("http://www.jxfczx.cn/report/K3_WinMessage.aspx", null);
		String beginString="<select name=\"ddlWinTermCode\"";
		String endString="</select>";
		res=subStartAndEnd(res, beginString, endString);
		List<String> listTerm = getListByStartEnd(res, "<option", "</option>");
		
		List<String> list=new ArrayList<>();
		for(String t:listTerm){
			list.add(subStartAndEnd(t, "value=\"", "\""));
		}
		return list;
	}
	private static String subStartAndEnd(String res,String beginString,String endString){
		String inputStart=beginString;
		String inputEnd=endString;
		int s = res.indexOf(inputStart);
		if(s >= 0){
			res=res.substring(s+inputStart.length());
			int end=res.indexOf(inputEnd);
			if(end >= 0){
				return res.substring(0,res.indexOf(inputEnd));
			}else{
				return null;
			}
		}
		return null;
	}
	private static List<String> getListByStartEnd(String res,String inputStart,String inputEnd){
		List<String> list=new ArrayList<>();
		int s = res.indexOf(inputStart);
		while(s >= 0){
			res=res.substring(s);
			int start=res.indexOf(inputStart);
			int end=res.indexOf(inputEnd);
			if(start >= 0 && end >= 0 && start<end){
				String input=res.substring(res.indexOf(inputStart), res.indexOf(inputEnd)+inputEnd.length());
				list.add(input);
				res=res.substring(res.indexOf(inputEnd)+inputEnd.length());
				s = res.indexOf(inputStart);
			}else{
				break;
			}
		}
		return list;
	}
}
