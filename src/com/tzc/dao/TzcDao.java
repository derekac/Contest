package com.tzc.dao;

import java.net.MalformedURLException;
import java.net.URL;

import net.jdiy.core.Dao;
import net.jdiy.core.JDiyContext;
import net.jdiy.util.Fs;

public class TzcDao {
	private static JDiyContext jdc;
	
	static {
		try {
			URL xmlLocation = Fs.getResource("jdiy.xml");
			String rootPath = Fs.getResource("../").toString();
			jdc = JDiyContext.newInstance(xmlLocation, rootPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public static Dao getDao(String dbName){
		return jdc.getDao(dbName);
	}
}
