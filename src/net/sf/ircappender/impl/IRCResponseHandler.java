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

/**
 * handles a response form the irc server
 *
 * @author hendrik
 */
interface IRCResponseHandler {

	/**
	 * handles an IRC server response
	 *
	 * @param ircConnection connection to the irc server
	 * @param line line sent by the server
	 */
	public void handleResponse(IrcConnection ircConnection, String line);
}
