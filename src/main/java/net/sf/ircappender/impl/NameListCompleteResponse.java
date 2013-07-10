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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * detects that the joining to the channel completed
 *
 * @author hendrik
 */
public class NameListCompleteResponse implements IRCResponseHandler {

	//:sendak.freenode.net 001 a1postman-bot-TE :Welcome to the freenode Internet Relay Chat Network a1postman-bot-TE
	private final Pattern pattern = Pattern.compile("(?i):[^ ]* 366 .*");

	public void handleResponse(IrcConnection ircConnection, String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			ircConnection.onChannelJoined();
		}
	}

}
