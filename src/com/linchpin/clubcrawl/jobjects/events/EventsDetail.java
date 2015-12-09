
package com.linchpin.clubcrawl.jobjects.events;


public class EventsDetail{
   	private String error;
   	private String pagecount;
   	private Results results;
   	private String totalcount;

 	public String getError(){
		return this.error;
	}
	public void setError(String error){
		this.error = error;
	}
 	public String getPagecount(){
		return this.pagecount;
	}
	public void setPagecount(String pagecount){
		this.pagecount = pagecount;
	}
 	public Results getResults(){
		return this.results;
	}
	public void setResults(Results results){
		this.results = results;
	}
 	public String getTotalcount(){
		return this.totalcount;
	}
	public void setTotalcount(String totalcount){
		this.totalcount = totalcount;
	}
}
