/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.project.web.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.cejug.yougi.project.business.ProjectBean;
import org.cejug.yougi.project.entity.Project;

@ManagedBean
@RequestScoped
public class ProjectMBean {

	@EJB
	private ProjectBean projectBean;
	
	private List<Project> projects;
	
	private Project project;
	
	@ManagedProperty(value="#{param.id}")
    private String id;

	public List<Project> getProjects() {
		if(this.projects == null) {
			this.projects = this.projectBean.findProjects();
		}
		return projects;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@PostConstruct
	public void load() {
		if(this.id != null && !this.id.isEmpty()) {
			this.project = projectBean.find(this.id);
		}
		else {
			this.project = new Project();
		}
	}
	
	public String save() {
		this.projectBean.save(this.project);
		return "projects";
	}
	
	public String remove() {
		this.projectBean.remove(this.id);
		return "projects";
	}
}