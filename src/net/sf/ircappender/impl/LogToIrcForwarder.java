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

import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author hendrik
 */
public class LogToIrcForwarder implements Runnable {
	private final Fifo eventQueue;
	private final IrcConnection ircConnection;
	private final long messageDelay;
	private final String channel;

	/**
	 * @param eventQueue
	 */
	public LogToIrcForwarder(IrcConnection ircConnection, Fifo eventQueue, long messageDelay, String channel) {
		this.ircConnection = ircConnection;
		this.eventQueue = eventQueue;
		this.messageDelay = messageDelay;
		this.channel = channel;
	}

	public void run() {

		while (ircConnection.isRunning()) {
			if (!ircConnection.isRoomEmpty()) {
				if (!(eventQueue == null) && !eventQueue.isEmpty()) {
					transferEntry();
				}
				try {
					Thread.sleep(messageDelay);
				} catch (Exception e) {
					// ignore
				}
			}
		}

		while (!eventQueue.isEmpty() && !ircConnection.isRoomEmpty()) {
			transferEntry();
		}
	}


	/**
	 * Transfers one LoggingEvent Entry from the eventQue to the ircMessage que
	*/
	private void transferEntry() {
		LoggingEvent le = eventQueue.pop();
		Object temp = le.getMessage();
		if (temp == null) {
			temp = "";
		}
		ircConnection.sendMessage(channel, temp.toString());
	}

}
