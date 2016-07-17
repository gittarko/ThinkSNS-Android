package com.thinksns.sociax.t4.model;

/**首页广告实体类，包含一个图片和一个 summary
 * * @author caoligai
 */
public class HomeAdsEntity {

	private byte[] image;
	
	private String summary;

	public HomeAdsEntity(byte[] image, String summary) {
		super();
		this.image = image;
		this.summary = summary;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
