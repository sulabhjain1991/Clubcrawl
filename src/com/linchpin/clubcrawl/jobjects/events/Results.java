
package com.linchpin.clubcrawl.jobjects.events;

import java.util.ArrayList;

import com.linchpin.clubcrawl.jobjects.tickets.Openingtimes;
import com.linchpin.clubcrawl.jobjects.tickets.Venue;

public class Results implements Comparable {
   
   	private String currency;
   
  
   
   	
   	private boolean gateway;
   	
	private String cancelled;
   	public String getCancelled()
	{
		return cancelled;
	}
	public void setCancelled(String cancelled)
	{
		this.cancelled = cancelled;
	}
	private String id;
   	private String eventname;
   	
  	private Venue venue;  	
   	private String imageurl;
	private String largeimageurl;
	private String link;
	private String date;
 	private String description;
 	private Openingtimes openingtimes;
 	private ArrayList<Genres> genres;
	private String entryprice;
	private ArrayList<Going> going;
   	private String imgoing;
	private ArrayList<Artists> artists;  
   	private String tickets;
   	
   	private String ticketsAvail;
 

 	public ArrayList<Artists> getArtists(){
		return this.artists;
	}
	public void setArtists(ArrayList<Artists> artists){
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
 	public ArrayList<Genres> getGenres(){
		return this.genres;
	}
	public void setGenres(ArrayList<Genres> genres){
		this.genres = genres;
	}
 	public ArrayList<Going> getGoing(){
		return this.going;
	}
	public void setGoing(ArrayList<Going> going){
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
 	public String getImgoing(){
		return this.imgoing;
	}
	public void setImgoing(String imgoing){
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
 	public String getTickets(){
		return this.tickets;
	}
	public void setTickets(String tickets){
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
	@Override
	public int compareTo(Object arg0)
	{
		return this.getEventname().compareTo(((Results) arg0).getEventname());
		
	}
}
