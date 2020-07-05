package com.lou.util;

import com.lou.pojo.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {

//    public static void main(String[] args) throws IOException {
//
//        HtmlParseUtil htmlParseUtil = new HtmlParseUtil();
//        List<Context> list = htmlParseUtil.parseUrl("大叔");
//
//        list.forEach(System.out::println);
//    }


    public List<Context> parseUrl(String keyWord) throws IOException {
        //        请求的路径
//        ajax的数据无法获取
        String url = "https://search.jd.com/Search?keyword=" + keyWord;
//      返回的document就是浏览器的document对象
        Document document = Jsoup.parse(new URL(url), 10000);

//        得到html的页面dom
        Element goodsList = document.getElementById("J_goodsList");
//        得到所有的li
        Elements lis = goodsList.getElementsByTag("li");

        List<Context> list=new ArrayList<>();


//        遍历所有的li
        for (Element li : lis) {
//            用来封装信息
            Context context=new Context();
//            得到图片信息
            String img = li.getElementsByTag("img").eq(0).attr("src");

//            得到价格
            String price = li.getElementsByClass("p-price").eq(0).text();

//            得到商品名字
            String name = li.getElementsByClass("p-name").eq(0).text();

            context.setName(name);
            context.setPrice(price);
            context.setImage(img);
            list.add(context);
        }
        return list;

    }
}
