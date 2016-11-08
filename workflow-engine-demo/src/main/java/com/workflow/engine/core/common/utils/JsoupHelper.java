package com.workflow.engine.core.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupHelper {

	/**
	 * 根据id获得值
	 * @param content
	 * @param id
	 * @return
	 */
	public static String getStringById(String content,String id){
		Document doc = Jsoup.parse(content);
		String title = doc.getElementById(id)!=null?doc.getElementById(id).val():"";
		return title;
	}
	
	/**
	 * 获得详情     
	 * @return
	 */
	public static Elements getContentElements(String htmlStr,String regex){
		
		Document doc = Jsoup.parse(htmlStr);
		Elements elements = doc.select(regex);
		
		return elements;
	}
	
	/**
	 * 获得详情根据class
	 * @return
	 */
	public static Elements getElementsByClass(String htmlStr,String classId){
		Document doc = Jsoup.parse(htmlStr);
		Elements elements = doc.getElementsByClass(classId);
		return elements;
	}
	
	/**
	 * 获得详情根据class
	 * @return
	 */
	public static Element getElementsById(String htmlStr,String id){
		Document doc = Jsoup.parse(htmlStr);
		Element element = doc.getElementById(id);
		return element;
	}

	public static void print(Object obj){
		System.out.println(obj);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
