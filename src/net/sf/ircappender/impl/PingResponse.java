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
 *
 * @author hendrik
 */
class PingResponse implements IRCResponseHandler {

	// < PING :calvino.freenode.net
	// > PONG :calvino.freenode.net
	private final Pattern pattern = Pattern.compile("(?i)^PING (.*)");

	public void handleResponse(IrcConnection ircConnection, String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			ircConnection.send("PONG " + matcher.group(1) + "\r\n");
		}
	}

}
