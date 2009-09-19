package com.explosivo.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import com.explosivo.jlibrary.data.Fifo;

/**
 * 
 * 
 * 
 * 
 * 
 * @author Administrator
 */
public class IrcAppender extends AppenderSkeleton {

	private static final boolean debug = true;

	private String host;
	private String username;
	private String password;
	private String channel;
	private String buffertype = "autopop";
	private String nickname;
	private int buffersize = 1000;
	private boolean isClosing;
	
	private Fifo eventQue = null;
	private IrcAppenderBot abt = null;
	private Thread botThread;
	
	/**
	 * Calls the superclass constructor.  No other logic at this time
	 */
	public IrcAppender() {
		super();
		if (debug) System.out.println("Irc Appender constructing");
	}


	/**
 	 * Initializes the system after the options are set
 	 * @see org.apache.log4j.spi.OptionHandler#activateOptions()
 	 */
	public void activateOptions ( ) {
	
		if (debug) System.out.println("Activate options");
		
		if (buffertype.equalsIgnoreCase("autopop")) {
			eventQue = new Fifo(1,buffersize, Fifo.AUTOPOP);		
		} else if (buffertype.equalsIgnoreCase("refuse")) {
			eventQue = new Fifo(1,buffersize, Fifo.REFUSE);
		} else {
			eventQue = new Fifo(1,buffersize, Fifo.AUTOPOP);
		}
				
		abt = doIrcBotInitialization();
		abt.setEventQue(eventQue);
		botThread= new Thread (abt);
		botThread.start();
	}

	/**
	 * Handles the initialization of the IrcBot object
	 * @return IrcAppenderBot the Initialized bot
	 */
	private IrcAppenderBot doIrcBotInitialization( ) {
		
		IrcAppenderBot bot = new IrcAppenderBot(nickname);
		
		if (debug) bot.setVerbose(true);
				
		bot.changeNick(nickname);

		try {
			
			if (password == null ) {
				bot.connect(this.getHost(), 6667);
			}
			else {
				bot.connect(this.getHost(), 6667, this.getPassword());
			}
								
			bot.joinChannel("#" + this.getChannel());

		}
		catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());		
		}

		bot.setChannel("#" + this.getChannel());
      
		return (bot);
		
	}



	/** 
	 * Recieves a LoggingEvent for handling.  Ignores if the appender is in the 
	 * process of closing down.
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent arg0) {
		if (! isClosing) eventQue.add(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return (false);
	}

	/** 
	 * Shuts down the appender.  Handles telling the bot to close and waiting for the bot
	 * to cleanly close its thread before continuing.
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
		isClosing = true;
		abt.setRunning(false);
		try {
			 botThread.join();
		} catch (Exception e) {
			 System.out.println("Exception on close join thing " + e.getMessage());			
		}
	}

	/**
	 * Returns the configured buffersize 
	 * @return double
	 */
	public int getBuffersize() {
		return buffersize;
	}

	/**
	 * Returns the configured channel
	 * @return String
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * Returns the configured host
	 * @return String
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the buffersize.
	 * @param buffersize The buffersize to set
	 */
	public void setBuffersize(int buffersize) {
		this.buffersize = buffersize;
		
	}

	/**
	 * Sets the channel.
	 * @param channel The channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * Sets the host.
	 * @param host The host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return String
	 */
	public String getBuffertype() {
		return buffertype;
	}

	/**
	 * Sets the buffertype.
	 * @param buffertype The buffertype to set
	 */
	public void setBuffertype(String buffertype) {
		this.buffertype = buffertype;
	}

	/**
	 * @return String
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Sets the nickname.
	 * @param nickname The nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}