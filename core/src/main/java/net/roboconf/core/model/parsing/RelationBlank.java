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

package net.roboconf.core.model.parsing;

/**
 * An instruction which designates a blank area (several line breaks, tabulations, etc).
 * @author Vincent Zurczak - Linagora
 */
public class RelationBlank extends AbstractIgnorableInstruction {

	/**
	 * Constructor.
	 * @param declaringFile
	 * @param content
	 */
	public RelationBlank( AbstractFile declaringFile, String content ) {
		super( declaringFile, content );
	}

	/*
	 * (non-Javadoc)
	 * @see net.roboconf.core.model.parsing.AbstractInstruction#getInstructionType()
	 */
	@Override
	public int getInstructionType() {
		return AbstractInstruction.BLANK;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Blank region";
	}
}