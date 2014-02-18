/**
 * IDM JSF Commons
 * Permission Create Controller
 * Copyright (C) 2014 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spectral.cc.core.portal.idm.controller.permission;

import com.spectral.cc.core.portal.idm.ccplugin.IDMJPAProviderConsumer;
import com.spectral.cc.core.portal.idm.controller.resource.ResourcesListController;
import com.spectral.cc.core.portal.idm.controller.role.RolesListController;
import com.spectral.cc.core.idm.commons.model.jpa.Permission;
import com.spectral.cc.core.idm.commons.model.jpa.Resource;
import com.spectral.cc.core.idm.commons.model.jpa.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionNewController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(PermissionNewController.class);

    private EntityManager em = IDMJPAProviderConsumer.getInstance().getIdmJpaProvider().createEM();

    private String name;
    private String description;

    private String resourceToBind;
    private Resource resource;

    private List<String> rolesToBind = new ArrayList<String>();
    private Set<Role> roles = new HashSet<Role>();

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceToBind() {
        return resourceToBind;
    }

    public void setResourceToBind(String resourceToBind) {
        this.resourceToBind = resourceToBind;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<String> getRolesToBind() {
        return rolesToBind;
    }

    public void setRolesToBind(List<String> rolesToBind) {
        this.rolesToBind = rolesToBind;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    private void bindSelectedResource() throws NotSupportedException, SystemException {
        for (Resource resource: ResourcesListController.getAll()) {
            if (resource.getName().equals(resourceToBind)) {
                resource = em.find(resource.getClass(),resource.getId());
                this.resource = resource;
                log.debug("Synced resource : {} {}", new Object[]{resource.getId(), resource.getName()});
                break;
            }
        }
    }

    private void bindSeletectedRoles() throws NotSupportedException, SystemException {
        for (Role role: RolesListController.getAll()) {
            for (String roleToBind : rolesToBind) {
                if (role.getName().equals(roleToBind)) {
                    this.roles.add(role);
                    log.debug("Synced role : {} {}", new Object[]{role.getId(), role.getName()});
                    break;
                }
            }
        }
    }

    public void save() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            bindSelectedResource();
            bindSeletectedRoles();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Exception raise while creating permission " + name + " !",
                                                       "Exception message : " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }

        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setResource(resource);
        permission.setRoles(roles);

        try {
            em.getTransaction().begin();
            em.persist(permission);
            this.resource.getPermissions().add(permission);
            em.merge(this.resource);
            for (Role role : permission.getRoles()) {
                role.getPermissions().add(permission);
                em.merge(role);
            }
            em.flush();
            em.getTransaction().commit();
            log.debug("Save new Permission {} !", new Object[]{name});
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                       "Permission created successfully !",
                                                       "Permission name : " + permission.getName());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Throwable t) {
            log.debug("Throwable catched !");
            t.printStackTrace();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                       "Throwable raised while creating permission " + permission.getName() + " !",
                                                       "Throwable message : " + t.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}