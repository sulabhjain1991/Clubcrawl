
package com.linchpin.clubcrawl.jobjects.tickets;

import java.util.List;

public class Results{
   	private List artists;
   	private String currency;
   	private String date;
   	private String description;
   	private String entryprice;
   	private String eventname;
   	private boolean gateway;
   	private List genres;
   	private List going;
   	private String id;
   	private String imageurl;
   	private Number imgoing;
   	private String largeimageurl;
   	private String link;
   	private Openingtimes openingtimes;
   	private boolean tickets;
   	private String ticketsAvail;
   	private Venue venue;

 	public List getArtists(){
		return this.artists;
	}
	public void setArtists(List artists){
		this.artists = artists;
	}
 	public String getCurrency(){
		return this.currency;
	}
	public void setCurrency(String currency){
		this.currency = currency;
	}
 	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date = date;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public String getEntryprice(){
		return this.entryprice;
	}
	public void setEntryprice(String entryprice){
		this.entryprice = entryprice;
	}
 	public String getEventname(){
		return this.eventname;
	}
	public void setEventname(String eventname){
		this.eventname = eventname;
	}
 	public boolean getGateway(){
		return this.gateway;
	}
	public void setGateway(boolean gateway){
		this.gateway = gateway;
	}
 	public List getGenres(){
		return this.genres;
	}
	public void setGenres(List genres){
		this.genres = genres;
	}
 	public List getGoing(){
		return this.going;
	}
	public void setGoing(List going){
		this.going = going;
	}
 	public String getId(){
		return this.id;
	}
	public void setId(String id){
		this.id = id;
	}
 	public String getImageurl(){
		return this.imageurl;
	}
	public void setImageurl(String imageurl){
		this.imageurl = imageurl;
	}
 	public Number getImgoing(){
		return this.imgoing;
	}
	public void setImgoing(Number imgoing){
		this.imgoing = imgoing;
	}
 	public String getLargeimageurl(){
		return this.largeimageurl;
	}
	public void setLargeimageurl(String largeimageurl){
		this.largeimageurl = largeimageurl;
	}
 	public String getLink(){
		return this.link;
	}
	public void setLink(String link){
		this.link = link;
	}
 	public Openingtimes getOpeningtimes(){
		return this.openingtimes;
	}
	public void setOpeningtimes(Openingtimes openingtimes){
		this.openingtimes = openingtimes;
	}
 	public boolean getTickets(){
		return this.tickets;
	}
	public void setTickets(boolean tickets){
		this.tickets = tickets;
	}
 	public String getTicketsAvail(){
		return this.ticketsAvail;
	}
	public void setTicketsAvail(String ticketsAvail){
		this.ticketsAvail = ticketsAvail;
	}
 	public Venue getVenue(){
		return this.venue;
	}
	public void setVenue(Venue venue){
		this.venue = venue;
	}
}
