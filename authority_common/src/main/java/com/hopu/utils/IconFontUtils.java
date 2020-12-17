package com.hopu.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图标工具类
 * @author Administrator
 *
 */
public class IconFontUtils {

	/**
	 * 获得图标
	 * @return
	 */
	public final static List<String> getIconFont(){
		List<String> data = new ArrayList<String>();
		try {
			Document document = Jsoup.parse(new File(PathUtils.getWebAppPath("static/iconfont/demo_index.html")), "utf-8");
			Element element = document.getElementsByClass("font-class").get(0);
			Elements elements = element.getElementsByClass("dib");
			for (Element element2 : elements) {
				data.add(element2.getElementsByClass("code-name").text().replace(".", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
