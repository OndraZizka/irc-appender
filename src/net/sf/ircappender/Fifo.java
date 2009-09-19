/*
   Copyright 2003-2009 IrcAppender project

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

import java.util.Vector;

import org.apache.log4j.spi.LoggingEvent;

/**
 * A first in first out buffer
 *
 * @author nhnb
 */
public class Fifo extends Vector {

	public static final int AUTOPOP = 1;
	public static final int REFUSE = 2;

	public Fifo(int i, int buffersize, int autopop2) {
		// TODO Auto-generated constructor stub
	}

	public LoggingEvent pop() {
		// TODO Auto-generated method stub
		return null;
	}

}
