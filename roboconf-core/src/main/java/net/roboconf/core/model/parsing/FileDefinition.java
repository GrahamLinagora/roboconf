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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.roboconf.core.model.ModelError;

/**
 * A bean that contains information about a configuration file.
 * @author Vincent Zurczak - Linagora
 */
public class FileDefinition {

	public static final int UNDETERMINED = 0;
	public static final int GRAPH = 1;
	public static final int INSTANCE = 2;
	public static final int AGGREGATOR = 3;

	private File editedFile;
	private int fileType = UNDETERMINED;
	private final URI fileLocation;
	private final List<ModelError> parsingErrors = new ArrayList<ModelError> ();
	private final List<AbstractBlock> blocks = new ArrayList<AbstractBlock> ();


	/**
	 * Constructor.
	 * @param editedFile not null
	 */
	public FileDefinition( File editedFile ) {
		this( editedFile != null ? editedFile.toURI() : null );
		this.editedFile = editedFile;
	}

	/**
	 * Constructor.
	 * @param fileLocation not null
	 */
	public FileDefinition( URI fileLocation ) {
		this.fileLocation = fileLocation;
	}

	/**
	 * @param editedFile the editedFile to set
	 */
	public void setEditedFile( File editedFile ) {
		this.editedFile = editedFile;
	}

	/**
	 * @return the editedFile
	 */
	public File getEditedFile() {
		return this.editedFile;
	}

	/**
	 * @return the fileLocation
	 */
	public URI getFileLocation() {
		return this.fileLocation;
	}

	/**
	 * @return the parsingErrors
	 */
	public List<ModelError> getParsingErrors() {
		return this.parsingErrors;
	}

	/**
	 * @return the blocks
	 */
	public List<AbstractBlock> getBlocks() {
		return this.blocks;
	}

	/**
	 * @return the file type, {@link FileDefinition#GRAPH} or {@link #INSTANCE}
	 */
	public int getFileType() {
		return this.fileType;
	}

	/**
	 * @param fileType the file type
	 * <p>
	 * {@link FileDefinition#GRAPH} or {@link #INSTANCE} or {@link #AGGREGATOR}
	 * </p>
	 */
	public void setFileType( int fileType ) {
		if( fileType != GRAPH
				&& fileType != INSTANCE
				&& fileType != AGGREGATOR )
			throw new IllegalArgumentException( "The file type was expected to be GRAPH, INSTANCE or AGGREGATOR." );

		this.fileType = fileType;
	}

	/**
	 * @return true if this file is a graph definition file
	 */
	public boolean isGraphFile() {
		return this.fileType == GRAPH;
	}

	/**
	 * @return true if this file is an instance definition file
	 */
	public boolean isInstanceFile() {
		return this.fileType == GRAPH;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String name = getFileLocation().getPath();
		int index = name.lastIndexOf( '/' );
		if( index > 0 )
			name = name.substring( index );

		StringBuilder sb = new StringBuilder();
		sb.append( name );
		sb.append( " with " );
		sb.append( this.blocks.size());
		sb.append( " blocks" );

		return sb.toString();
	}

	/**
	 * Transforms a file type into a string.
	 * @param fileType a file type
	 * @return a string version of the file type (never null)
	 */
	public static String fileTypeAsString( int fileType ) {

		String result;
		switch( fileType ) {
		case AGGREGATOR:
			result = "aggregator";
			break;

		case GRAPH:
			result = "graph";
			break;

		case INSTANCE:
			result = "intsnace";
			break;

		case UNDETERMINED:
			result = "undetermined";
			break;

		default:
			result = "unknown";
			break;
		}

		return result;
	}
}
