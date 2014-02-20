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

package net.roboconf.dm.rest.client.mocks;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.roboconf.core.model.helpers.ComponentHelpers;
import net.roboconf.core.model.helpers.InstanceHelpers;
import net.roboconf.core.model.runtime.Application;
import net.roboconf.core.model.runtime.Component;
import net.roboconf.core.model.runtime.Instance;
import net.roboconf.dm.rest.RestUtils;
import net.roboconf.dm.rest.api.IApplicationWs;

/**
 * @author Vincent Zurczak - Linagora
 */
@Path( IApplicationWs.PATH )
public class ApplicationWsMock implements IApplicationWs {

	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.api.IApplicationWs
	 * #listAllInstances(java.lang.String)
	 */
	@Override
	public List<Instance> listAllInstances( String applicationName ) {

		List<Instance> result = new ArrayList<Instance> ();
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null )
			result.addAll( InstanceHelpers.getAllInstances( app ));

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.api.IApplicationWs
	 * #listRootInstances(java.lang.String)
	 */
	@Override
	public List<Instance> listRootInstances( String applicationName ) {

		List<Instance> result = new ArrayList<Instance> ();
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null )
			result.addAll( app.getRootInstances());

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.api.IApplicationWs
	 * #listChildrenInstances(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Instance> listChildrenInstances( String applicationName, String instancePath ) {

		List<Instance> result = new ArrayList<Instance> ();
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		Instance inst = null;
		if( app != null
				&& ( inst = RestUtils.findInstanceFromRestfulPath( app, instancePath )) != null )
			result.addAll( inst.getChildren());

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IInstanceWs
	 * #addRootInstance(java.lang.String, net.roboconf.core.model.runtime.Instance)
	 */
	@Override
	public Response addRootInstance( String applicationName, Instance instance ) {

		Response response;
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app == null ) {
			response = Response.status( Status.NOT_FOUND ).entity( "The application " + applicationName + " was not found." ).build();

		} else if( InstanceHelpers.tryToInsertChildInstance( app, null, instance )) {
			response = Response.ok().build();
			// In real implementation, think also to notify the agents

		} else {
			response = Response.status( Status.NOT_ACCEPTABLE ).build();
		}

		return response;
	}


	/* (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IInstanceWs
	 * #addInstance(java.lang.String, java.lang.String, net.roboconf.core.model.runtime.Instance)
	 */
	@Override
	public Response addInstance( String applicationName, String parentInstancePath, Instance instance ) {

		Response response;
		Instance parentInstance = null;
		Application app = new ManagementWsMock().getApplicationByName( applicationName );

		if( app == null ) {
			response = Response.status( Status.NOT_FOUND ).entity( "The application " + applicationName + " was not found." ).build();

		} else if(( parentInstance = RestUtils.findInstanceFromRestfulPath( app, parentInstancePath )) == null ) {
			response = Response.status( Status.NOT_FOUND ).entity( "The parent instance was nout found." ).build();

		} else {
			if( InstanceHelpers.tryToInsertChildInstance( app, parentInstance, instance )) {
				response = Response.ok().build();
				// In real implementation, think also to notify the agents

			} else {
				response = Response.status( Status.NOT_ACCEPTABLE ).build();
			}
		}

		return response;
	}


	/* (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IInstanceWs
	 * #getInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public Instance getInstance( String applicationName, String instancePath ) {

		Instance result = null;
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null )
			result = RestUtils.findInstanceFromRestfulPath( app, instancePath );

		return result;
	}


	/* (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IInstanceWs
	 * #removeInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public Response removeInstance( String applicationName, String instancePath ) {

		Instance instance = null;
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null )
			instance = RestUtils.findInstanceFromRestfulPath( app, instancePath );

		if( instance != null ) {
			// In real implementation, think also to notify the agents
			if( instance.getParent() != null )
				instance.getParent().getChildren().remove( instance );
			else
				app.getRootInstances().remove( instance );
		}

		return Response.ok().build();
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IGraphWs
	 * #listComponents(java.lang.String)
	 */
	@Override
	public List<Component> listComponents( String applicationName ) {

		List<Component> result = new ArrayList<Component> ();
		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null )
			result.addAll( ComponentHelpers.findAllComponents( app ));

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IGraphWs
	 * #findPossibleComponentChildren(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> findPossibleComponentChildren( String applicationName, String instancePath ) {

		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		Instance instance = null;
		if( app != null )
			instance = RestUtils.findInstanceFromRestfulPath( app, instancePath );

		List<String> result = new ArrayList<String> ();
		if( instance != null
				&& instance.getComponent() != null ) {

			for( Component c : instance.getComponent().getChildren())
				result.add( c.getName());
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IGraphWs
	 * #findPossibleParentInstances(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> findPossibleParentInstances( String applicationName, String componentName ) {

		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		List<String> result = new ArrayList<String> ();

		// Run through all the instances.
		// See if their component can support a child "of type componentName".
		for( Instance inst : app.getRootInstances()) {
			for( Instance i : InstanceHelpers.buildHierarchicalList( inst )) {
				for( Component c : i.getComponent().getChildren()) {

					if( componentName.equals( c.getName())) {
						String instancePath = InstanceHelpers.computeInstancePath( i );
						result.add( instancePath );
					}
				}
			}
		}

		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see net.roboconf.dm.rest.client.exceptions.server.IGraphWs
	 * #createInstanceFromComponent(java.lang.String, java.lang.String)
	 */
	@Override
	public Instance createInstanceFromComponent( String applicationName, String componentName ) {

		Instance result = new Instance( "new" );
		Component comp = null;

		Application app = new ManagementWsMock().getApplicationByName( applicationName );
		if( app != null
				&& app.getGraphs() != null )
			comp = ComponentHelpers.findComponent( app.getGraphs(), componentName );

		// In the real implementation, properties should be set in the exports.
		// The interest of this operation is to display default export values
		// in a web console (as an example) and thus allow to override them if necessary.
		result.setComponent( comp );
		return result;
	}
}