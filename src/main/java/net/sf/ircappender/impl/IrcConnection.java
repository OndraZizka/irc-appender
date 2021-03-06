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
package net.sf.ircappender.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

/**
 * a very simple IRC bot
 *
 * @author nhnb
 */
public class IrcConnection {
	private final String server;
	private final int port;
	private final boolean ssl;
	private final String user;
	private final String nick;
	private final String password;
	private final String channel;

	private Socket socket;
	BufferedReader input;
	private BufferedWriter output;

	final List handlers;
	final Set members;

	private String currentNick;
	private int nickCounter = 0;
	private long messageDelay = 1000;

	Thread forwarderThread = null;
	boolean waitingForReconnect = false;
	boolean debug = false;
	boolean isRunning;
	Fifo eventQueue;

	/**
	 * create a new IRC bot
	 *
	 * @param server   the irc server
	 * @param port     the irc port
	 * @param ssl      use SSL security
	 * @param user     username of bot
	 * @param password password of bot
	 * @param nick     nickname of bot
	 * @param channel  channel to join
	 */
	public IrcConnection(String server, int port, boolean ssl, String user, String password, String nick,
			String channel) {
		this.server = server;
		this.port = port;
		this.ssl = ssl;
		this.user = user;
		this.password = password;
		this.nick = nick;
		this.channel = channel;
		this.handlers = new LinkedList();
		handlers.add(new JoinResponse());
		handlers.add(new NamesResponse());
		handlers.add(new NameListCompleteResponse());
		handlers.add(new NickAcceptedResponse());
		handlers.add(new NickInUseResponse());
		handlers.add(new PartResponse());
		handlers.add(new PingResponse());
		this.members = new HashSet();
	}

	/**
	 * connects to the specified IRC server
	 *
	 * @throws UnknownHostException in case of an invalid irc server name
	 * @throws IOException in case of an unexpected input/output error
	 */
	public void connect() throws UnknownHostException, IOException {
		this.waitingForReconnect = false;
		if (this.ssl) {
			socket = SSLSocketFactory.getDefault().createSocket(server, port);
		} else {
			socket = new Socket(server, port);
		}
		members.clear();
		currentNick = nick;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

		Thread thread = new Thread(new IrcResponseDispatcher(this), "IrcAppender: Reader");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * disconnects from the IRC server. It is save to call this mehod even if not connected
	 */
	public void disconnect() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// ignore
			}
			socket = null;
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				// ignore
			}
		}
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * logs in to IRC
	 */
	public void login() {
		if (password != null) {
			send("PASS " + password);
		}
		send("NICK " + currentNick);
		String username = user;
		if (user == null) {
			username = nick;
		}
		send("USER " + username + " \"\" \"\" \"\"");
	}

	/**
	 * sends a message to a channel or nickname
	 *
	 * @param target   channel or nickname to send to
	 * @param message  message to send
	 */
	public void sendMessage(String target, String message) {
		send("PRIVMSG " + target + " :" + message);
	}

	void send(String line) {
		// TODO: strip line breaks
		if (debug) {
			System.out.println("> " + line);
		}
		try {
			output.write(line + "\r\n");
			output.flush();
		} catch (IOException e) {
			onUnexpectedDisconnect();
		}
	}

	void addMember(String member) {
		members.add(member);
	}

	void removeMember(String member) {
		members.remove(member);
	}

	/**
	 * checks whether the channel is empty
	 *
	 * @return true, if only the bot is in the channel
	 */
	public boolean isChannelEmpty() {
		// ignore ourself
		return members.size() <= 1;
	}


	void onUnexpectedDisconnect() {
		this.disconnect();
		if (!waitingForReconnect) {
			waitingForReconnect = true;

			// do not user TimerTask to be compatible with really old java versions
			final Thread t = new Thread("IrcAppender: wait for reconnect") {

				public void run() {
					try {
						Thread.sleep(60 * 1000);
						connect();
						login();
					} catch (final Exception e) {
						// not good
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
	}

	/**
	 * picks a different nickname
	 */
	public void onNickInUse() {
		nickCounter++;
		currentNick = "a" + nickCounter + nick;
		send("NICK " + currentNick);
	}

	/**
	 * handles the acceptance of the nick name.
	 *
	 * This method will check if the nick name is the desired one and join the channel.
	 * If the nickname is different, it will try to get nickserv to kill the other connection
	 * which is using the nickname.
	 */
	public void onNickAccepted() {
		if (!currentNick.equals(nick) && (password != null)) {
			sendMessage("NickServ", "ghost " + nick + " " + password);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// ignore
			}
			send("NICK " + nick);
			sendMessage("NickServ", "identify " + nick + " " + password);
		}
		send("JOIN " + channel);
	}

	/**
	 * starts the forwarder thread
	 */
	public void onChannelJoined() {
		forwarderThread = new Thread(new LogToIrcForwarder(this, eventQueue, messageDelay, channel), "IrcAppender: Writer");
		forwarderThread.setDaemon(true);
		forwarderThread.start();
	}

	/**
	 * Sets the debug flag
	 *
	 * @param debug debug flag
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Returns the running state of the thread
	 *
	 * @return boolean
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Sets the isRunning.
	 *
	 * @param isRunning The isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * Sets the event queue.
	 *
	 * @param eventQueue The event queue to set
	 */
	public void setEventQueue(Fifo eventQueue) {
		this.eventQueue = eventQueue;
	}

	/**
	 * Sets the message delay
	 *
	 * @param messageDelay The message delay to set
	 */
	public void setMessageDelay(long messageDelay) {
		this.messageDelay = messageDelay;
	}

	/**
	 * waits for the forwarder thread to finish
	 */
	public void waitForForwardThreadToFinish() {
		if (forwarderThread != null) {
			try {
				forwarderThread.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
}
