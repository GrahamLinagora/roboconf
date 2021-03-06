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

package net.roboconf.core.model.runtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import net.roboconf.core.internal.utils.Utils;

/**
 * An application groups an identifier, graph definitions and instances.
 * @author Vincent Zurczak - Linagora
 */
public class Application implements Serializable {

	private static final long serialVersionUID = -4753958407033243184L;

	private String name, qualifier, description;
	private Graphs graphs;
	private final Collection<Instance> rootInstances = new LinkedHashSet<Instance> ();


	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * @return the qualifier
	 */
	public String getQualifier() {
		return this.qualifier;
	}

	/**
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier( String qualifier ) {
		this.qualifier = qualifier;
	}

	/**
	 * @return the graphs
	 */
	public Graphs getGraphs() {
		return this.graphs;
	}

	/**
	 * @param graphs the graphs to set
	 */
	public void setGraphs( Graphs graphs ) {
		this.graphs = graphs;
	}

	/**
	 * @return the root instances
	 */
	public Collection<Instance> getRootInstances() {
		return this.rootInstances;
	}

	@Override
	public boolean equals( Object obj ) {
		return obj instanceof Application
				&& Utils.areEqual( this.name, ((Application) obj ).getName())
				&& Utils.areEqual( this.qualifier, ((Application) obj ).getQualifier());
	}

	@Override
	public int hashCode() {
		int i1 = this.name == null ? 29 : this.name.hashCode();
		int i2 = this.qualifier == null ? 11 : this.qualifier.hashCode();
		return i1 * i2;
	}
}
