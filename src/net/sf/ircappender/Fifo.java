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

import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.log4j.spi.LoggingEvent;

/**
 * A first in, first out buffer
 *
 * @author nhnb
 */
public class Fifo {

	/** remove the oldest element if the fifo is full */
	public static final int AUTOPOP = 1;
	/** ignore the request to add more elements if the fifo is full */
	public static final int REFUSE = 2;
	
	private Vector elements = new Vector();
	private int bufferSize;
	private int fullStrategy;

	/**
	 * creates a new Fifo
	 *
	 * @param bufferSize   the maximum size of the fifo
	 * @param fullStrategy what to do when the size limit is exceeded
	 */
	public Fifo(int bufferSize, int fullStrategy) {
		this.bufferSize = bufferSize;
		this.fullStrategy = fullStrategy;
	}

	/**
	 * gets the oldest element
	 *
	 * @return oldest element
	 * @throws NoSuchElementException in case the fifo is empty
	 */
	public LoggingEvent pop() throws NoSuchElementException {
		return (LoggingEvent) elements.remove(0);
	}

	/**
	 * adds an element. if the fifo is full either the oldest element is removed
	 * or the request to add this element is ignored, depending on fullStrategy
	 *
	 * @param e element to add
	 */
	public void add(LoggingEvent e) {
		if (elements.size() < bufferSize) {
			elements.add(e);
		} else {
			if (fullStrategy == AUTOPOP) {
				elements.remove(0);
				elements.add(e);
			}
		}
	}

	/**
	 * is the fifo empty?
	 *
	 * @return <code>true</code> if the <code>fifo</code> is empty, false otherwise
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * gets the current size of the fifo (this is the number of stored elements, not maximum size).
	 *
	 * @return size
	 */
	public int size() {
		return elements.size();
	}
}
