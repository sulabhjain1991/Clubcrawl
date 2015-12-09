package com.linchpin.clubcrawl.beans;

import java.util.List;

public class BeanListGalleryData 
{
	String message;
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	List<BeanGalleryData> gallery;

	public List<BeanGalleryData> getGallery() {
		return gallery;
	}

	public void setGallery(List<BeanGalleryData> gallery) {
		this.gallery = gallery;
	}
}
