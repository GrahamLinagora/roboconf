/**
 * Copyright 2014 Linagora, Université Joseph Fourier
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

package net.roboconf.core.internal.model.parsing;

import java.util.Iterator;

import net.roboconf.core.model.parsing.AbstractBlock;
import net.roboconf.core.model.parsing.AbstractBlockHolder;
import net.roboconf.core.model.parsing.BlockBlank;
import net.roboconf.core.model.parsing.BlockComment;
import net.roboconf.core.model.parsing.BlockComponent;
import net.roboconf.core.model.parsing.BlockFacet;
import net.roboconf.core.model.parsing.BlockImport;
import net.roboconf.core.model.parsing.BlockInstanceOf;
import net.roboconf.core.model.parsing.BlockProperty;
import net.roboconf.core.model.parsing.ParsingConstants;
import net.roboconf.core.model.parsing.FileDefinition;
import net.roboconf.core.model.validators.ParsingModelValidator;

/**
 * A class to serialize a relations model.
 * @author Vincent Zurczak - Linagora
 */
public class FileDefinitionSerializer {

	private String lineSeparator = System.getProperty( "line.separator" );


	/**
	 * Constructor.
	 */
	public FileDefinitionSerializer() {
		// nothing
	}

	/**
	 * Constructor.
	 * @param lineSeparator the line separator (ignored if null)
	 */
	public FileDefinitionSerializer( String lineSeparator ) {
		if( lineSeparator != null )
			this.lineSeparator = lineSeparator;
	}


	/**
	 * @param definitionFile the relations file
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( FileDefinition definitionFile, boolean writeComments ) {

		StringBuilder sb = new StringBuilder();
		for( Iterator<AbstractBlock> it = definitionFile.getBlocks().iterator(); it.hasNext(); ) {
			sb.append( write( it.next(), writeComments ));
			if( it.hasNext())
				sb.append( this.lineSeparator );
		}

		return sb.toString();
	}


	/**
	 * @param block an blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( AbstractBlock block, boolean writeComments ) {

		String result;
		switch( block.getInstructionType()) {
		case AbstractBlock.COMPONENT:
			result = write((BlockComponent) block, writeComments );
			break;

		case AbstractBlock.FACET:
			result = write((BlockFacet) block, writeComments );
			break;

		case AbstractBlock.INSTANCEOF:
			result = write((BlockInstanceOf) block, writeComments, 0 );
			break;

		case AbstractBlock.IMPORT:
			result = write((BlockImport) block, writeComments );
			break;

		case AbstractBlock.PROPERTY:
			result = write((BlockProperty) block, writeComments );
			break;

		case AbstractBlock.COMMENT:
			result = write((BlockComment) block, writeComments );
			break;

		case AbstractBlock.BLANK:
			result = write((BlockBlank) block, writeComments );
			break;

		default:
			result = null;
			break;
		}

		return result;
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockImport block, boolean writeComments ) {

		StringBuilder sb = new StringBuilder();
		sb.append( "import " );
		sb.append( block.getUri());
		sb.append( ";" );

		if( writeComments
				&& block.getInlineComment() != null )
			sb.append( block.getInlineComment());

		return sb.toString();
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockComponent block, boolean writeComments ) {
		return writePropertiesHolder( block, writeComments, 0 );
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockFacet block, boolean writeComments ) {
		return ParsingConstants.KEYWORD_FACET + " " + writePropertiesHolder( block, writeComments, 0 );
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @param indentationLevel the indentation level
	 * @return a string (never null)
	 */
	public String write( BlockInstanceOf block, boolean writeComments, int indentationLevel ) {

		StringBuilder sb = new StringBuilder();
		indent( sb, indentationLevel );
		sb.append( ParsingConstants.KEYWORD_INSTANCE_OF );
		sb.append( " " );
		sb.append( writePropertiesHolder( block, writeComments, indentationLevel ));

		return sb.toString();
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockBlank block, boolean writeComments ) {

		StringBuilder sb = new StringBuilder();
		if( writeComments ) {
			// Invalid blank sections can be "repaired" even if they are invalid.
			// Here, we replace it by a line break.
			// The error is signaled as a warning in the validation.
			// Useful if a blank section was built programmatically.
			if( ParsingModelValidator.validate( block ).isEmpty())
				sb.append( block.getContent());
			else
				sb.append( this.lineSeparator );
		}

		return sb.toString();
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockProperty block, boolean writeComments ) {

		StringBuilder sb = new StringBuilder();
		sb.append( block.getName());
		sb.append( ": " );
		sb.append( block.getValue());
		sb.append( ";" );

		if( writeComments
				&& block.getInlineComment() != null )
			sb.append( block.getInlineComment());

		return sb.toString();
	}


	/**
	 * @param block the blockuction
	 * @param writeComments true to write comments
	 * @return a string (never null)
	 */
	public String write( BlockComment block, boolean writeComments ) {

		StringBuilder sb = new StringBuilder();
		if( writeComments ) {
			// Invalid comment sections can be "repaired" even if they are invalid.
			// Here, we insert a comment delimiter where needed.
			// The error is signaled as a warning in the validation.
			// Useful if a comment was built programmatically.
			if( ParsingModelValidator.validate( block ).isEmpty()) {
				sb.append( block.getContent());

			} else {
				for( String s : block.getContent().split( "\n" )) {
					if( s.trim().startsWith( ParsingConstants.COMMENT_DELIMITER ))
						sb.append( "# " );

					sb.append( s );
					sb.append( "\n" );
				}
			}
		}

		return sb.toString();
	}


	/**
	 * @param holder the blockuction
	 * @param writeComments true to write comments
	 * @param indentationLevel the indentation level
	 * @return a string (never null)
	 */
	private String writePropertiesHolder( AbstractBlockHolder holder, boolean writeComments, int indentationLevel ) {

		StringBuilder sb = new StringBuilder();
		sb.append( holder.getName());
		sb.append( " {" );

		if( writeComments
				&& holder.getInlineComment() != null )
			sb.append( holder.getInlineComment());

		for( AbstractBlock block : holder.getInnerBlocks()) {
			sb.append( this.lineSeparator );
			if( block.getInstructionType() == AbstractBlock.INSTANCEOF ) {
				sb.append( write((BlockInstanceOf) block, writeComments, indentationLevel + 1 ));

			} else {
				if( block.getInstructionType() == AbstractBlock.PROPERTY )
					indent( sb, indentationLevel + 1 );

				sb.append( write( block, writeComments ));
			}
		}

		sb.append( this.lineSeparator );
		indent( sb, indentationLevel );
		sb.append( "}" );
		if( writeComments
				&& holder.getClosingInlineComment() != null )
			sb.append( holder.getClosingInlineComment());

		return sb.toString();
	}


	/**
	 * @param sb
	 * @param indentationLevel
	 */
	private void indent( StringBuilder sb, int indentationLevel ) {
		for( int i=0; i<indentationLevel; i++ )
			sb.append( "\t" );
	}
}
