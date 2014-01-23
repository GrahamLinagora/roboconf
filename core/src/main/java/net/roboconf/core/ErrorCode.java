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

package net.roboconf.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * All the error codes that can be encountered with a Roboconf model.
 * @author Vincent Zurczak - Linagora
 */
public enum ErrorCode {

	// Parsing Errors
	P_IO_ERROR( ErrorLevel.SEVERE, ErrorCategory.PARSING, "An I/O exception occurred, parsing was interrupted." ),
	P_ONE_BLOCK_PER_LINE( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Extra-characters were found after the semi-colon. You cannot have more than one block per line." ),
	P_PROPERTY_ENDS_WITH_SEMI_COLON( ErrorLevel.SEVERE, ErrorCategory.PARSING, "A property definition must end with a semi-colon." ),
	P_IMPORT_ENDS_WITH_SEMI_COLON( ErrorLevel.SEVERE, ErrorCategory.PARSING, "An import declaration must end with a semi-colon." ),
	P_O_C_BRACKET_EXTRA_CHARACTERS( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Extra-characters were found after the opening curly bracket." ),
	P_C_C_BRACKET_EXTRA_CHARACTERS( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Extra-characters were found after the closing curly bracket." ),
	P_O_C_BRACKET_MISSING( ErrorLevel.SEVERE, ErrorCategory.PARSING, "A facet, component or instance name must be followed by an opening curly bracket." ),
	P_C_C_BRACKET_MISSING( ErrorLevel.SEVERE, ErrorCategory.PARSING, "A facet, component or instance declaration must end with a closing curly bracket." ),
	P_UNRECOGNIZED_BLOCK( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Unrecognized block. 'import', 'facet', 'instanceof' or a component name were expected." ),
	P_INVALID_PROPERTY( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Invalid content. A property was expected." ),
	P_INVALID_PROPERTY_OR_INSTANCE( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Invalid content. A property or an instance was expected." ),
	P_INVALID_FILE_TYPE( ErrorLevel.SEVERE, ErrorCategory.PARSING, "Invalid file type. It mixes facet, component and instance definitions." ),
	P_NO_FILE_TYPE( ErrorLevel.SEVERE, ErrorCategory.PARSING, "No file type. No import, facet, component or instance definition was found." ),

	// Parsing Model Errors
	PM_INVALID_BLOCK_TYPE( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Validation failed. Unknown block type." ),
	PM_UNKNOWN_PROPERTY_NAME( ErrorLevel.WARNING, ErrorCategory.PARSING_MODEL, "Unknown property name for a facet or a component." ),
	PM_FORBIDDEN_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "This name is not allowed for a facet or a component." ),
	PM_EMPTY_PROPERTY_VALUE( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The property value is missing." ),
	PM_EMPTY_IMPORT_LOCATION( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The import location must be specified." ),
	PM_EMPTY_FACET_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The facet name is missing." ),
	PM_EMPTY_REFERENCED_FACET_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "A facet name is missing." ),
	PM_EMPTY_COMPONENT_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The component name is missing." ),
	PM_EMPTY_VARIABLE_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "A variable name is missing." ),

	PM_INVALID_CHILD_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid child name. As a reminder, children names must be separated by a comma." ),
	PM_INVALID_FACET_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid facet name. Facet names must be separated by a comma." ),
	PM_INVALID_COMPONENT_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid component name. Component names must be separated by a comma." ),
	PM_INVALID_EXPORTED_VAR_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid variable name. Exported variable names must be separated by a comma." ),
	PM_INVALID_IMPORTED_VAR_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid variable name. Imported variable names must be separated by a comma." ),
	PM_INVALID_INSTALLER_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Invalid installer name." ),
	PM_INVALID_ICON_LOCATION( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The icon location must end with an image extension (gif, jpg, jpeg, png)." ),
	PM_INVALID_INDEX_REFERENCE_USE( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The name contains ${index} but the cardinality property is not set." ),
	PM_INVALID_INSTANCE_ELEMENT( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "An instance can only contain properties, other instances, blank lines or comments." ),
	PM_INVALID_INSTANCE_CARDINALITY( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "A cardinality must be a positive integer." ),

	PM_PROPERTY_NOT_APPLIABLE( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "This property does not apply to this element." ),
	PM_DUPLICATE_PROPERTY( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "This property was already set for this element." ),
	PM_MISSING_ALIAS_PROPERTY( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The 'alias' property is missing." ),
	PM_MISSING_INSTANCE_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The 'name' property is missing." ),
	PM_MISSING_INDEX_REFERENCE( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "The 'cardinality' property is used but the name does not contain ${index}." ),

	PM_DOT_IS_NOT_ALLOWED( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Component and facet names cannot contain a dot." ),
	PM_INCOMPLETE_IMPORTED_VAR_NAME( ErrorLevel.SEVERE, ErrorCategory.PARSING_MODEL, "Incomplete variable name. Imported variable names must be prefixed by a component or facet name, followed by a dot." ),
	PM_MALFORMED_COMMENT( ErrorLevel.WARNING, ErrorCategory.PARSING_MODEL, "A comment delimiter is missing. It will be added at serialization time." ),
	PM_MALFORMED_BLANK( ErrorLevel.WARNING, ErrorCategory.PARSING_MODEL, "A blank section contains non-blank characters. It will be ignored at serialization time." ),

	// Conversion Errors
	CO_UNREACHABLE_FILE( ErrorLevel.SEVERE, ErrorCategory.CONVERSION, "A configuration file could not be read." ),
	CO_UNRESOLVED_FACET( ErrorLevel.SEVERE, ErrorCategory.CONVERSION, "The facet could not be resolved. It was not declared anywhere." ),
	CO_CYCLE_IN_FACETS( ErrorLevel.SEVERE, ErrorCategory.CONVERSION, "A cycle was found in facet definitions." ),
	CO_ALREADY_DEFINED_FACET( ErrorLevel.SEVERE, ErrorCategory.CONVERSION, "This facet was already defined." ),
	CO_ALREADY_DEFINED_COMPONENT( ErrorLevel.SEVERE, ErrorCategory.CONVERSION, "This component was already defined." ),

	// Runtime Model Errors


	// Execution Errors
	;


	/**
	 * Error categories.
	 */
	public enum ErrorCategory {
		PARSING, PARSING_MODEL, CONVERSION, RUNTIME_MODEL, EXECUTION;
	}

	/**
	 * Error levels.
	 */
	public enum ErrorLevel {
		SEVERE, WARNING;
	}


	// These instructions are called after the enumeration items have been created.
	private static final Map<ErrorCategory,AtomicInteger> CAT_TO_ID = new HashMap<ErrorCategory,AtomicInteger> ();
	static {
		for( ErrorCategory cat : ErrorCategory.values())
			CAT_TO_ID.put( cat, new AtomicInteger( 0 ));

		for( ErrorCode code : values())
			code.errorId = CAT_TO_ID.get( code.category ).getAndIncrement();
	}

	private final String msg;
	private final ErrorCategory category;
	private final ErrorLevel level;
	private int errorId;


	/**
	 * @param level
	 * @param category
	 * @param msg
	 */
	private ErrorCode( ErrorLevel level, ErrorCategory category, String msg ) {
		this.msg = msg;
		this.category = category;
		this.level = level;
	}

	/**
	 * @return the category
	 */
	public ErrorCategory getCategory() {
		return this.category;
	}

	/**
	 * @return the level
	 */
	public ErrorLevel getLevel() {
		return this.level;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return this.msg;
	}

	/**
	 * @return the errorId
	 */
	public int getErrorId() {
		return this.errorId;
	}
}