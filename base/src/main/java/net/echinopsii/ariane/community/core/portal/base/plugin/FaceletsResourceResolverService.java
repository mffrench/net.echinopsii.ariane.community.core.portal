/**
 * Portal base bundle
 * Facelets Resource Resolver Service Interface
 * Copyright (C) 2013 Mathilde Ffrench
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

package net.echinopsii.ariane.community.core.portal.base.plugin;

import java.net.URL;

/**
 * Provide access to portal facelets resource from web application which are using their own facelets resource resolver (IE : not the portal one).
 */
public interface FaceletsResourceResolverService {
    /**
     * resolve facelets resource from portal wab or bundles implementing this service
     *
     * @param path path of the resource from implementing bundle
     *
     * @return the resource URL (null if not found)
     */
    public URL resolveURL(String path);
}