package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;


/** 
 * 类说明：   
 * @author  wz    
 * @date    2014-12-11
 * @version 1.0
 */
public class ModelBanner extends SociaxItem {
	 int bannerId;
	 boolean isActive=false;//是否有效
	 public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public ModelBanner(JSONObject data){
		 try{
			 if(data.has("banner")){
				 this.setBannerId(data.getInt("banner"));
			 }
			 if(data.has("bannerurl")){
				 this.setBannerurl(data.getString("bannerurl"));
			 }
			 if(data.has("bannerpic")){
				 this.setBannerpic(data.getString("bannerpic"));
			 }
			 if(data.has("is_active")){
				 this.setActive(data.getString("is_active").equals("1"));
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 public ModelBanner(){
	 }
	 public int getBannerId() {
		return bannerId;
	}

	public void setBannerId(int bannerId) {
		this.bannerId = bannerId;
	}

	public String getBannerurl() {
		return bannerurl;
	}

	public void setBannerurl(String bannerurl) {
		this.bannerurl = bannerurl;
	}

	public String getBannerpic() {
		return bannerpic;
	}

	public void setBannerpic(String bannerpic) {
		this.bannerpic = bannerpic;
	}

	String bannerurl,bannerpic;
	 

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

}
