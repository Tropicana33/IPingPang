package com.tropicana.ipingpang.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;






public class VideoUtils {
	public static class Video {
		  private String flash;
		  private String pic;
		  private String time;
		  public String getFlash() {
		  return flash;
		  }
		  public void setFlash(String flash) {
		  this.flash = flash;
		  }
		  public String getPic() {
		  return pic;
		  }
		  public void setPic(String pic) {
		  this.pic = pic;
		  }
		  public String getTime() {
		  return time;
		  }
		  public void setTime(String time) {
		  this.time = time;
		  }
		  }
	
	public static Video getYouKuVideo(String url) throws Exception{  
        Document doc = getURLContent(url);  
          
        /**
        *获取视频缩略图 
        */  
        String pic = getElementAttrById(doc, "s_sina", "href");  
        int local = pic.indexOf("pic=");  
        pic = pic.substring(local+4);  
          
        /**
        * 获取视频地址
        */       
        String flash = getElementAttrById(doc, "link2", "value");  
          
        /**
        * 获取视频时间
        */    
        String time = getElementAttrById(doc, "download", "href");  
        String []arrays = time.split("\\|");  
        time = arrays[4];  
          
        Video video=new Video(); 
        video.setPic(pic);  
        video.setFlash(flash);  
        video.setTime(time);  
          
        return video;  
    }  
	
	/**
	* 获取网页的内容
	*/
	 private static Document getURLContent(String url) throws Exception{  
	        Document doc = (Document) Jsoup.connect(url).data("query", "Java")  
	          .userAgent("Mozilla")  
	          .cookie("auth", "token")  
	          .timeout(6000)  
	          .post();  
	        return doc;  
	    }  
	
	 /**
	 * 根据HTML的ID键及属于名，获取属于值
	 * @param id HTML的ID键
	 * @param attrName 属于名
	 * @return 返回属性值
	 */
	  private static String getElementAttrById(Document doc, String id, String attrName)throws Exception{  
	        Element et = doc.getElementById(id);  
	        String attrValue = et.attr(attrName);  
	          
	        return attrValue;  
	    }  
	  
	  /**
	  * 获取script某个变量的值
	  * @param name 变量名称
	  * @return 返回获取的值 
	  */
	  private static String getScriptVarByName(String name, String content){  
	        String script = content;  
	          
	        int begin = script.indexOf(name);  
	          
	        script = script.substring(begin+name.length()+2);  
	          
	        int end = script.indexOf(",");  
	          
	        script = script.substring(0,end);  
	          
	        String result=script.replaceAll("'", "");  
	        result = result.trim();  
	          
	        return result;  
	    }  
	  
	  private static String getVideoTime(Document doc, String url, String id) {  
	        String time = null;  
	          
	        Element timeEt = doc.getElementById(id);   
	        Elements links = timeEt.select("dt > a");  
	          
	          
	        for (Element link : links) {  
	          String linkHref = link.attr("href");  
	          if(linkHref.equalsIgnoreCase(url)){  
	              time = link.parent().getElementsByTag("em").first().text();  
	              break;  
	          }  
	        }  
	        return time;  
	    }  
	  
	  
}
