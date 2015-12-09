package com.linchpin.clubcrawl.beans;

public class BeanWhosOutVenue 
{
	private String id;
	private String label;
	private String img;
	private String header_img;
	public String getHeader_img() {
		return header_img;
	}
	public void setHeader_img(String header_img) {
		this.header_img = header_img;
	}
	private String address;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
