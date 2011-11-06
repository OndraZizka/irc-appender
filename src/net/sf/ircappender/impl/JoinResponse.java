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
 * handles people joning the channel to keep track of the members
 *
 * @author hendrik
 */
class JoinResponse implements IRCResponseHandler {

	// :hendrik!~hendrik@stendhalgame.org JOIN #arianne-test
	private final Pattern pattern = Pattern.compile("(?i):([^!@])+[^ ]* JOIN (.*)");

	public void handleResponse(IrcConnection ircConnection, String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			ircConnection.addMember(matcher.group(1));
		}
	}

}
