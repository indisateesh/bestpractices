package com.gigaspaces.server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * There is one TimeWriter per site.  It basically
 * is a simple clock, writing ticks (TimeStamps) into
 * the local site.
 * 
 * @author DeWayne
 *
 */
public class TimeWriter {
	private static Logger log=Logger.getLogger(TimeWriter.class.getName());
	private String location;
	private Long interval=1000L;
	private GigaSpace space=null;
	Timer timer=new Timer();
	
	public String getSiteName() {
		return location;
	}
	
	@Required
	public void setLocation(String siteName) {
		this.location = siteName;
	}
	public Long getInterval() {
		return interval;
	}
	public void setInterval(Long interval) {
		this.interval = interval;
	}

	//init-method
	private void run(){
		timer.schedule(new TimeWorker(space,location),0,interval);
	}
	
	//destroy-method
	private void stop(){
		if(timer!=null)timer.cancel();
	}
	
	public GigaSpace getSpace() {
		return space;
	}

	@Required
	public void setSpace(GigaSpace space) {
		this.space = space;
	}
}

class TimeWorker extends TimerTask{
	private static final Logger log=Logger.getLogger(TimeWorker.class.getName());
	private final GigaSpace space;
	private final String location;
	
	public TimeWorker(GigaSpace space,String loc){
		this.space=space;
		this.location=loc;
	}
	@Override
	@Transactional
	public void run() {
		space.write(new TimeStamp(location));
	}
}
