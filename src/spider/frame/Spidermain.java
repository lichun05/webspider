package spider.frame;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.select.Elements;


public class Spidermain {
	public static void main(String[] args) {	
		String proName = "北京";
		HashMap<String,String> areas = getAreas();
		if (areas.get(proName) != null) {
			System.out.println(proName);
			
			HashMap<String, String> hospitals =  getProvinceHospitals(proName, areas.get(proName));
			String hName  = "中日医院";
			String hUrl = hospitals.get(hName);
			HashMap<String, String> hDepartments = getDepartmentsInHospital(hName, hUrl);
			
			System.out.println(hDepartments.get("消化内科"));
			System.out.println(hDepartments.get("呼吸内科"));
		}
		else {
			System.out.println("省份错误");
		}
	}
	
	//获得所有的地区信息
	private static HashMap<String,String> getAreas() 
	{
		HashMap<String, String> provinceInfos = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect("http://www.haodf.com").get();
			Elements ele = doc.select(".find_dq");
			
			if (ele != null) {
//				System.out.println(ele.toString());
				Iterator<Element> it = ele.iterator();
				while (it.hasNext()) {
					Element areainfo = it.next();
					Elements provinces = areainfo.select("li a");
					Iterator<Element> pit = provinces.iterator();
 					while (pit.hasNext()) {
						Element province = pit.next();
						String addr = province.attr("href").toString();
						String proName = province.text();
						provinceInfos.put(proName, addr);
					}
 					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(provinceInfos.size());
		return provinceInfos;
	}
	
	//获取某个地区的所有医院
	private static HashMap<String, String> getProvinceHospitals(String name, String urlStr) 
	{
		HashMap<String, String> hospitals = new HashMap<String, String>();
		try {
			Document proDoc = Jsoup.connect(urlStr).get();
			Elements proeles = proDoc.getElementsByAttributeValueStarting("href", "http://www.haodf.com/hospital");
			Iterator<Element> it = proeles.iterator();
			while (it.hasNext()) {
				Element hospitalInfo = it.next();
				if (!hospitalInfo.hasAttr("class")) {
//					System.out.println(hospitalInfo.toString());
					hospitals.put(hospitalInfo.text(), hospitalInfo.attr("href"));	
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println(name +"地区医院数量：" +hospitals.size());
		return hospitals;
	}
	
	private static HashMap<String, String> getDepartmentsInHospital(String hname, String hurl) {
		HashMap<String, String> departments = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect(hurl).get();
			Elements dlist = doc.getElementsByAttributeValueStarting("href", "http://www.haodf.com/faculty");
			Iterator<Element> dit = dlist.iterator();
			while(dit.hasNext()) {
				Element departmentInfo = dit.next();
				String durl = departmentInfo.attr("href").toString();
				String dname = departmentInfo.text();
				departments.put(dname, durl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return departments;
	}
	
}
