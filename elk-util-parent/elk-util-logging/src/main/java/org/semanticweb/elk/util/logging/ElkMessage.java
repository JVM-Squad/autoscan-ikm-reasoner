/*
 * #%L
 * ELK Utilities for Logging
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.util.logging;

import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Enhanced message class that carries information to classify the message, in
 * addition to the actual messages string.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkMessage {

	protected final String message;
	protected final String messageType;

	/**
	 * Create a new message. The message can be an arbitrary string, possibly
	 * with linebreaks (this is encouraged for very long messages). The
	 * messageType should be a global identifier for the kind of message,
	 * preferably human-readable and starting lowercase, such as
	 * "unsupportedFeature". Dots could be used to relate a message type to a
	 * Java package/class, but mentioning ELK is not useful (start only after
	 * "org.semantiweb.elk").
	 * 
	 * @param message
	 * @param messageType
	 */
	public ElkMessage(String message, String messageType) {
		this.message = message;
		this.messageType = messageType;
	}

	public String getMessage() {
		return message;
	}

	public String getMessageType() {
		return messageType;
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(message, messageType);
	}

}
