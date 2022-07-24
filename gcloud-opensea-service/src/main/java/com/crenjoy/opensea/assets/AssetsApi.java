package com.crenjoy.opensea.assets;

import java.io.IOException;

import com.crenjoy.opensea.util.HttpUtil;
import com.crenjoy.opensea.util.HttpUtil.HttpException;


public class AssetsApi {

	public static String findAssets(String baseUrl) throws HttpException, IOException {
		String url=baseUrl+"/api/v1/assets?format=json";
		return HttpUtil.sendGet(url);
	}
	
	
	public static void main(String[] args) throws HttpException, IOException {
		String a= AssetsApi.findAssets("https://testnets-api.opensea.io");
		System.out.println(a);
	}

	
}
