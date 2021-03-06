/**
 * Copyright 2013-2014 Linagora, Université Joseph Fourier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.messaging.messages.from_dm_to_agent;

import net.roboconf.messaging.messages.Message;

/**
 * @author Noël - LIG
 * FIXME: is it used yet?
 */
public class MsgCmdFirewallAdd extends Message {

	private static final long serialVersionUID = 2087714421499704920L;

	private final String sourceIp;
	private final String destIp;
	private final int port;


	/**
	 * Constructor.
	 * @param sourceIp
	 * @param destIp
	 * @param port
	 */
	public MsgCmdFirewallAdd( String sourceIp, String destIp, int port ) {
		super();
		this.sourceIp = sourceIp;
		this.destIp = destIp;
		this.port = port;
	}

	/**
	 * @return the sourceIp
	 */
	public String getSourceIp() {
		return this.sourceIp;
	}

	/**
	 * @return the destIp
	 */
	public String getDestIp() {
		return this.destIp;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}
}
