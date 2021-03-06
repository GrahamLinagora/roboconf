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

package net.roboconf.core.model.converters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.roboconf.core.Constants;
import net.roboconf.core.ErrorCode;
import net.roboconf.core.internal.model.parsing.FileDefinitionParser;
import net.roboconf.core.internal.utils.ModelUtils;
import net.roboconf.core.model.ModelError;
import net.roboconf.core.model.helpers.ComponentHelpers;
import net.roboconf.core.model.helpers.InstanceHelpers;
import net.roboconf.core.model.helpers.VariableHelpers;
import net.roboconf.core.model.parsing.AbstractBlock;
import net.roboconf.core.model.parsing.AbstractBlockHolder;
import net.roboconf.core.model.parsing.BlockImport;
import net.roboconf.core.model.parsing.BlockInstanceOf;
import net.roboconf.core.model.parsing.BlockProperty;
import net.roboconf.core.model.parsing.FileDefinition;
import net.roboconf.core.model.runtime.Graphs;
import net.roboconf.core.model.runtime.Instance;

/**
 * To build a collection of {@link Instance} from a {@link FileDefinition}.
 * @author Vincent Zurczak - Linagora
 */
public class FromInstanceDefinition {

	private final FileDefinition definition;
	private final Collection<ModelError> errors = new ArrayList<ModelError> ();

	private Map<String,BlockImport> importUriToImportDeclaration;
	private Map<String,List<BlockInstanceOf>> rootInstanceNameToBlocks;
	private Set<String> alreadyProcessedUris;
	private Collection<Instance> rootInstances;
	private Graphs graphs;


	/**
	 * Constructor.
	 * @param definition its type must be either {@link FileDefinition#INSTANCE} or {@link FileDefinition#AGGREGATOR}
	 */
	public FromInstanceDefinition( FileDefinition definition ) {
		if( definition.getFileType() != FileDefinition.AGGREGATOR
				&& definition.getFileType() != FileDefinition.INSTANCE )
			throw new IllegalArgumentException( "File must be of type INSTANCE or AGGREGATOR." );

		this.definition = definition;
	}


	/**
	 * @return the errors (never null)
	 */
	public Collection<ModelError> getErrors() {
		return this.errors;
	}


	/**
	 * @param graphs the graph(s)
	 * @return a non-null collection of root rootInstances wrapped in machines
	 * <p>
	 * The result is not significant if there are errors.<br />
	 * Conversion errors are available by using {@link #getErrors()}.
	 * </p>
	 */
	public Collection<Instance> buildInstances( Graphs graphs ) {

		// Initialize collections
		this.importUriToImportDeclaration = new HashMap<String,BlockImport> ();
		this.rootInstanceNameToBlocks = new HashMap<String,List<BlockInstanceOf>> ();
		this.alreadyProcessedUris = new HashSet<String> ();
		this.rootInstances = new ArrayList<Instance> ();
		this.graphs = graphs;
		this.errors.clear();

		// Process the file and its imports
		processInstructions( this.definition );
		this.alreadyProcessedUris.add( String.valueOf( this.definition.getFileLocation()));

		while( ! this.importUriToImportDeclaration.isEmpty()) {
			Entry<String,BlockImport> entry = this.importUriToImportDeclaration.entrySet().iterator().next();
			String uri = entry.getKey();
			this.importUriToImportDeclaration.remove( uri );
			this.alreadyProcessedUris.add( uri );

			if( this.alreadyProcessedUris.contains( uri ))
				continue;

			// Load the file
			FileDefinition importedDefinition;
			try {
				importedDefinition = new FileDefinitionParser( uri, true ).read();

			} catch( URISyntaxException e ) {
				ModelError error = new ModelError( ErrorCode.CO_UNREACHABLE_FILE, 0 );
				error.setDetails( "Import location: " + uri );
				this.errors.add( error );
				continue;
			}

			// Check the file type
			if( this.definition.getFileType() != FileDefinition.AGGREGATOR
					&& this.definition.getFileType() != FileDefinition.GRAPH ) {

				ModelError error = new ModelError( ErrorCode.CO_NOT_A_GRAPH, 0 );
				error.setDetails( "Imported file  " + uri + " is of type " + FileDefinition.fileTypeAsString( this.definition.getFileType()) + "." );
				this.errors.add( error );
				continue;
			}

			// Process the file
			processInstructions( importedDefinition );
		}

		// Check uniqueness
		if( this.errors.isEmpty())
			checkUnicity();
		
		// Set real exports
		for( Instance rootInstance : rootInstances ) {
			for( Instance inst : InstanceHelpers.buildHierarchicalList( rootInstance ))
				inst.getExports().putAll( InstanceHelpers.getExportedVariables( inst ));
		}
			
		return this.rootInstances;
	}


	private void processInstructions( FileDefinition definition ) {

		for( AbstractBlock block : this.definition.getBlocks()) {
			switch( block.getInstructionType()) {
			case AbstractBlock.INSTANCEOF:
				processInstance((BlockInstanceOf) block, definition.getFileLocation());
				break;

			case AbstractBlock.IMPORT:
				processImport((BlockImport) block, definition.getFileLocation());
				break;

			default:
				// nothing
				break;
			}
		}
	}


	private void processImport( BlockImport block, URI processedUri ) {
		String uri = block.getUri().trim();

		// FIXME: to deal with...
		// try {
		//	uri = UriUtils.buildNewURI( processedUri, uri ).toString();
			this.importUriToImportDeclaration.put( uri, block );

//		} catch( URISyntaxException e ) {
//			throw new ConversionException( "An URI could not be built from the import definition.",  e, block );
//		}
	}


	private void processInstance( BlockInstanceOf block, URI processedUri ) {

		// Process the rootInstances
		Map<BlockInstanceOf,Instance> blockToInstance = new LinkedHashMap<BlockInstanceOf,Instance> ();
		Instance rootInstance = new Instance();
		blockToInstance.put( block, rootInstance );
		while( ! blockToInstance.isEmpty()) {

			// The current one to process won't be processed again
			Map.Entry<BlockInstanceOf,Instance> entry = blockToInstance.entrySet().iterator().next();
			blockToInstance.remove( entry.getKey());

			// Process the current
			BlockInstanceOf currentBlock = entry.getKey();
			Instance instance = entry.getValue();
			instance.setName( ModelUtils.getPropertyValue( currentBlock, Constants.PROPERTY_INSTANCE_NAME ));
			instance.setChannel( ModelUtils.getPropertyValue( currentBlock, Constants.PROPERTY_INSTANCE_CHANNEL ));
			instance.setComponent( ComponentHelpers.findComponent( this.graphs, currentBlock.getName()));

			for( AbstractBlock innerBlock : currentBlock.getInnerBlocks()) {

				// Check overridden exports
				if( innerBlock.getInstructionType() == AbstractBlock.PROPERTY ) {
					String pName = ((BlockProperty) innerBlock).getName();
					if( Constants.PROPERTY_INSTANCE_NAME.equals( pName )
							|| Constants.PROPERTY_INSTANCE_NAME.equals( pName ))
						continue;

					String pValue = ((BlockProperty) innerBlock).getValue();
					resolveOverriddenExport( innerBlock, instance, pName, pValue );
					continue;
				}

				// Initialize children rootInstances
				if( innerBlock.getInstructionType() != AbstractBlock.INSTANCEOF )
					continue;

				Instance newInstance = new Instance();
				InstanceHelpers.insertChild( instance, newInstance );
				blockToInstance.put((BlockInstanceOf) innerBlock, newInstance );
			}
		}

		// Track the root instance
		this.rootInstances.add( rootInstance );
		List<BlockInstanceOf> instanceBlocks = this.rootInstanceNameToBlocks.get( rootInstance.getName());
		if( instanceBlocks == null )
			instanceBlocks = new ArrayList<BlockInstanceOf> ();

		this.rootInstanceNameToBlocks.put( rootInstance.getName(), instanceBlocks );
	}


	private void checkUnicity() {

		for( Map.Entry<String,List<BlockInstanceOf>> entry : this.rootInstanceNameToBlocks.entrySet()) {
			if( entry.getValue().size() == 1 )
				continue;

			StringBuilder sb = new StringBuilder();
			sb.append( "Root instance " );
			sb.append( entry.getKey());
			sb.append( " is defined in:\n" );
			for( AbstractBlockHolder holder : entry.getValue()) {
				sb.append( " - " );

				FileDefinition file = holder.getDeclaringFile();
				if( file.getEditedFile() != null )
					sb.append( file.getEditedFile().getName());
				else
					sb.append( file.getFileLocation());

				sb.append( " - line " );
				sb.append( holder.getLine());
				sb.append( "\n" );
			}

			for( AbstractBlockHolder holder : entry.getValue()) {
				ModelError error = new ModelError( ErrorCode.CO_ALREADY_DEFINED_ROOT_INSTANCE, holder.getLine());
				error.setDetails( sb.toString());
				this.errors.add( error );
			}
		}

		// The map is useless now
		this.rootInstanceNameToBlocks = null;
	}


	private void resolveOverriddenExport( AbstractBlock holder, Instance instance, String varName, String varValue ) {

		// Component variables are prefixed by a component or a facet name.
		// Instance variables may not be prefixed (user-friendly).
		Set<String> ambiguousNames = new HashSet<String> ();
		for( String componentVarName : instance.getComponent().getExportedVariables().keySet()) {

			// If variables have the same name (by ignoring the prefixing component or facet name)...
			// ... then we have an ambiguity.
			if( varName.equals( VariableHelpers.parseVariableName( componentVarName ).getValue()))
				ambiguousNames.add( componentVarName );
		}

		// Analyze the result
		// No name? Show a warning and it
		if( ambiguousNames.isEmpty()) {
			ModelError error = new ModelError( ErrorCode.CO_NOT_OVERRIDING, holder.getLine());
			error.setDetails( "Variable name:" + varName );
			this.errors.add( error );
		}

		// A single name: mark it as resolved
		else if( ambiguousNames.size() == 1 )
			instance.getOverriddenExports().put( ambiguousNames.iterator().next(), varValue );

		// Several names? Mark an ambiguity we cannot solve.
		else {
			StringBuilder sb = new StringBuilder();
			sb.append( "Variable " );
			sb.append( varName );
			sb.append( " could mean " );

			for( Iterator<String> it = ambiguousNames.iterator(); it.hasNext(); ) {
				sb.append( it.next());
				if( it.hasNext())
					sb.append( ", " );
			}

			ModelError error = new ModelError( ErrorCode.CO_AMBIGUOUS_OVERRIDING, holder.getLine());
			error.setDetails( sb.toString());
			this.errors.add( error );
		}
	}
}
