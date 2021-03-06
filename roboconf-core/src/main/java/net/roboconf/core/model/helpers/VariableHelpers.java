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

package net.roboconf.core.model.helpers;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.roboconf.core.model.runtime.Instance;

/**
 * Helpers related to variables.
 * @author Vincent Zurczak - Linagora
 */
public final class VariableHelpers {

	private static final String IP = "ip";



	/**
	 * Private empty constructor.
	 */
	private VariableHelpers() {
		// nothing
	}


	/**
	 * Parses a variable name (&lt;facetOrComponentName&gt;.&lt;simpleName&gt;).
	 * <p>
	 * All the variables (imported, or exported - after resolution) must be
	 * prefixed by a component or facet name.
	 * </p>
	 * <p>
	 * If the variable name was not prefixed by a component or a facet name, then
	 * the couple ( "", &lt; originalVariableName &gt; ) is returned.
	 * </p>
	 *
	 * @param variableName a variable name (not null)
	 * @return a map entry (key = facet or component name, value = simple name)
	 */
	public static Map.Entry<String,String> parseVariableName( String variableName ) {

		String componentOrFacetName = "", simpleName = variableName;
		int index = variableName.indexOf( '.' );
		if( index >= 0 ) {
			componentOrFacetName = variableName.substring( 0, index ).trim();
			simpleName = variableName.substring( index + 1 ).trim();
		}

		return new AbstractMap.SimpleEntry<String,String>( componentOrFacetName, simpleName );
	}


	/**
	 * Parses an exported variable (&lt;variableName&gt; = &lt;defaultValue&gt;).
	 * <p>
	 * The equal symbol and default value are optional.
	 * </p>
	 *
	 * @param exportedVariable an exported variable (not null)
	 * @return a map entry (key = variable name, value = default value)
	 */
	public static Map.Entry<String,String> parseExportedVariable( String exportedVariable ) {

		int index = exportedVariable.indexOf( '=' );
		String varName = exportedVariable, defaultValue = null;
		if( index > 0 ) {
			varName = exportedVariable.substring( 0, index ).trim();
			defaultValue = exportedVariable.substring( index + 1 ).trim();
		}

		return new AbstractMap.SimpleEntry<String,String>( varName, defaultValue );
	}


	/**
	 * Determines whether an instance imports a variable associated with a given component or facet.
	 * @param instance the instance to check (not null)
	 * @param componentOrFacetName a component or facet name
	 * @return true if such a variable was found, false otherwise
	 */
	public static boolean instanceHasVariablesWithPrefix( Instance instance, String componentOrFacetName ) {

		boolean result = false;
		for( String importedVar : instance.getComponent().getImportedVariables().keySet()) {
			if( importedVar.startsWith( componentOrFacetName + "." )) {
				result = true;
				break;
			}
		}

		return result;
	}


	/**
	 * Finds the component and facet names that prefix the variables of an instance.
	 * @param instance an instance
	 * @return a non-null set with all the component and facet names this instance exports
	 */
	public static Set<String> findExportedVariablePrefixes( Instance instance ) {
		Set<String> result = new HashSet<String> ();

		Map<String,String> instanceExports = InstanceHelpers.getExportedVariables( instance );
		for( String exportedVariableName : instanceExports.keySet())
			result.add( VariableHelpers.parseVariableName( exportedVariableName ).getKey());

		return result;
	}


	/**
	 * Finds the component and facet names that prefix the variables of an instance.
	 * <p>
	 * Optional imports are skipped.
	 * </p>
	 *
	 * @param instance an instance
	 * @return a non-null set with all the component and facet names this instance imports
	 */
	public static Set<String> findImportedVariablePrefixes( Instance instance ) {
		Set<String> result = new HashSet<String> ();

		for( Map.Entry<String,Boolean> entry : instance.getComponent().getImportedVariables().entrySet()) {
			if( ! entry.getValue())
				result.add( VariableHelpers.parseVariableName( entry.getKey()).getKey());
		}

		return result;
	}


	/**
	 * Updates the exports of an instance with network values.
	 * <p>
	 * For the moment, only IP is supported.
	 * </p>
	 *
	 * @param instanceExports a non-null map of instance exports
	 * @param ipAddress the IP address to set
	 */
	public static void updateNetworkVariables( Map<String,String> instanceExports, String ipAddress ) {

		// Find the keys to update ( xxx.ip )
		Set<String> keysToUpdate = new HashSet<String> ();
		for( Map.Entry<String,String> entry : instanceExports.entrySet()) {
			String suffix = parseVariableName( entry.getKey()).getValue();
			if( IP.equals( suffix ))
				keysToUpdate.add( entry.getKey());
		}

		// Update them
		for( String key : keysToUpdate )
			instanceExports.put( key, ipAddress );
	}
}
