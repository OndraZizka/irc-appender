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

import java.util.Iterator;

/**
 *
 * @author hendrik
 */
class IrcResponseDispatcher implements Runnable {


	/**
	 *
	 */
	private final IrcConnection ircConnection;

	/**
	 * @param ircConnection
	 */
	IrcResponseDispatcher(IrcConnection ircConnection) {
		this.ircConnection = ircConnection;
	}

	public void run() {
		ircConnection.isRunning = true;
		try {
			String line = ircConnection.input.readLine();
			while (ircConnection.isRunning && !this.ircConnection.waitingForReconnect && (line != null)) {
				if (ircConnection.debug) {
					System.out.println("< " + line);
				}
				Iterator itr = ircConnection.handlers.iterator();
				while (itr.hasNext()) {
					IRCResponseHandler handler = (IRCResponseHandler) itr.next();
					handler.handleResponse(ircConnection, line);
				}
				line = ircConnection.input.readLine();
			}
		} catch (java.io.IOException e) {
			// ignore
		}
		ircConnection.isRunning = false;
		ircConnection.onUnexpectedDisconnect();
	}
}