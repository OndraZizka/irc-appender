/*
   Copyright 2011 Faiumoni e. V.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package net.sf.ircappender;

import java.io.IOException;
import java.net.UnknownHostException;

import net.sf.ircappender.impl.Fifo;
import net.sf.ircappender.impl.IrcConnection;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * appends log-events to an irc channel.
 */
public class IrcHandler extends Handler {

	private boolean debug = false;
	private String host;
	private int port = 6667;
	private boolean ssl = false;
	private String username;
	private String password;
	private String channel;
	private String buffertype = "autopop";
	private String nickname;
	private long messageDelay = 1000;
	private int buffersize = 1000;
	private boolean isClosing;
	private Fifo eventQueue = null;
	private IrcConnection irc = null;

	/**
	 * Initializes the system after the options are set
	 */
	public IrcHandler() {
		configure();

		if (debug) {
			System.out.println("Activate options");
		}

		if (buffertype.equalsIgnoreCase("autopop")) {
			eventQueue = new Fifo(buffersize, Fifo.AUTOPOP);
		} else if (buffertype.equalsIgnoreCase("refuse")) {
			eventQueue = new Fifo(buffersize, Fifo.REFUSE);
		} else {
			eventQueue = new Fifo(buffersize, Fifo.AUTOPOP);
		}

		irc = doIrcBotInitialization();
		irc.setEventQueue(eventQueue);
		try {
			irc.connect();
			irc.login();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the initialization of the IrcBot object
	 * 
	 * @return IrcAppenderBot the Initialized bot
	 */
	private IrcConnection doIrcBotInitialization() {
		IrcConnection bot = new IrcConnection(host, port, ssl, username,
				password, nickname, channel);
		bot.setDebug(debug);
		bot.setMessageDelay(messageDelay);
		return bot;
	}

	/**
	 * Recieves a LoggingEvent for handling. Ignores if the appender is in the
	 * process of closing down.
	 *
	 * @param logRecord logging event
	 */
	public void publish(LogRecord logRecord) {
		if (!isClosing) {
			String temp;
			Object object = logRecord.getMessage();
			if (object == null) {
				temp = "";
			} else {
				temp = object.toString();
			}
			eventQueue.add(temp);
		}
	}

	/**
	 * Shuts down the appender. Handles telling the bot to close and waiting for
	 * the bot to cleanly close its thread before continuing.
	 */
	public void close() {
		isClosing = true;
		irc.setRunning(false);
		irc.waitForForwardThreadToFinish();
		irc.disconnect();
	}

	/**
	 * Returns the configured buffersize
	 * 
	 * @return double
	 */
	public int getBuffersize() {
		return buffersize;
	}

	/**
	 * Returns the configured channel
	 * 
	 * @return String
	 */
	public String getChannel() {
		if ((channel != null) && !channel.startsWith("#")) {
			channel = "#" + channel;
		}
		return channel;
	}

	/**
	 * is debugging enabled
	 * 
	 * @return true, if debugging is enabled, false otherwise
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * enables or disables debugging messages
	 * 
	 * @param debug
	 *            should debug message be written to stdout?
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Returns the configured host
	 * 
	 * @return String
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Returns the configured irc server port
	 * 
	 * @return int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Is SSL used to secure the connection to the IRC server?
	 * 
	 * @return ssl ussage
	 */
	public boolean isSsl() {
		return ssl;
	}

	/**
	 * Enables or disables the use of SSL for the IRC connection
	 * 
	 * @param ssl
	 *            true to enable SSL security
	 */
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	/**
	 * gets the IRC password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * gets the IRC username
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the buffersize.
	 * 
	 * @param buffersize
	 *            The buffersize to set
	 */
	public void setBuffersize(int buffersize) {
		this.buffersize = buffersize;

	}

	/**
	 * Sets the channel.
	 * 
	 * @param channel
	 *            The channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            The host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Sets the irc server port
	 * 
	 * @param port
	 *            port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * gets the name of the buffer strategy
	 * 
	 * @return String
	 */
	public String getBuffertype() {
		return buffertype;
	}

	/**
	 * Sets the buffer strategy.
	 * 
	 * @param buffertype
	 *            The buffertype to set
	 */
	public void setBuffertype(String buffertype) {
		this.buffertype = buffertype;
	}

	/**
	 * gets the IRC nickname of the bot
	 * 
	 * @return nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Sets the nickname.
	 * 
	 * @param nickname
	 *            The nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Set the delay between messages. Note that some IRC servers have a rate
	 * limit which may be stricter.
	 * 
	 * @param messageDelay
	 *            in milliseconds
	 */
	public void setMessageDelay(long messageDelay) {
		this.messageDelay = messageDelay;
	}

	/**
	 * gets the delay between multiple messages
	 * 
	 * @return long delay in milliseconds
	 */
	public long getMessageDelay() {
		return messageDelay;
	}

	public void flush() {
		// do noghting
	}


	/**
	 * configures the log handler
	 */
	private void configure() {
		LogManager manager = LogManager.getLogManager();
		String cname = getClass().getName();

		debug = Boolean.parseBoolean(get(manager, cname + ".debug", "false"));
		host = manager.getProperty(cname + ".host");
		port = Integer.parseInt(get(manager, cname + ".port", "6667"));
		ssl = Boolean.parseBoolean(get(manager, cname + ".ssl", "false"));
		username = manager.getProperty(cname + ".username");
		password = manager.getProperty(cname + ".password");
		channel = manager.getProperty(cname + ".channel");
		buffertype = get(manager, cname + ".bufferType", get(manager, cname + ".buffertype", "autopop"));
		nickname = manager.getProperty(cname + ".nickname");
		messageDelay = Integer.parseInt(get(manager, cname + ".messageDelay", get(manager, cname + ".messagedelay", "1000")));
		buffersize = Integer.parseInt(get(manager, cname + ".bufferSize", get(manager, cname + ".buffersize", "1000")));
	}

	/**
	 * gets a configuation setting
	 *
	 * @param manager LogManager
	 * @param name name of configuration setting
	 * @param defaultValue default value
	 * @return configuration value, or default value
	 */
	private String get(LogManager manager, String name, String defaultValue) {
		String value = manager.getProperty(name);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
}
