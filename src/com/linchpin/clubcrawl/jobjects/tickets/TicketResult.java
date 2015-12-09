
package com.linchpin.clubcrawl.jobjects.tickets;

import java.util.ArrayList;

public class TicketResult{
   	private Number error;
   	private Number pagecount;
   	private ArrayList<Results> results;
   	private String totalcount;

 	public Number getError(){
		return this.error;
	}
	public void setError(Number error){
		this.error = error;
	}
 	public Number getPagecount(){
		return this.pagecount;
	}
	public void setPagecount(Number pagecount){
		this.pagecount = pagecount;
	}
 	public ArrayList<Results> getResults(){
		return this.results;
	}
	public void setResults(ArrayList<Results> results){
		this.results = results;
	}
 	public String getTotalcount(){
		return this.totalcount;
	}
	public void setTotalcount(String totalcount){
		this.totalcount = totalcount;
	}
}
