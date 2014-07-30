/**
 *	Copyright [2013] [www.cuubez.com]
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */

package com.cuubez.core.provider;

import com.cuubez.core.util.CaseInsensitiveMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


public class BuiltResponse extends Response {
    protected Object entity;
    protected int status = HttpServletResponse.SC_OK;
    protected CaseInsensitiveMap<Object> metadata = new CaseInsensitiveMap<Object>();
    protected Class entityClass;
    protected Type genericType;

    public BuiltResponse() {
    }

    public BuiltResponse(int status, CaseInsensitiveMap<Object> metadata, Object entity) {
        setEntity(entity);
        this.status = status;
        this.metadata = metadata;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setEntity(Object entity) {
        if (entity == null) {
            this.entity = null;
            this.genericType = null;
            this.entityClass = null;
        } else if (entity instanceof GenericEntity) {

            GenericEntity ge = (GenericEntity) entity;
            this.entity = ge.getEntity();
            this.genericType = ge.getType();
            this.entityClass = ge.getRawType();
        } else {
            this.entity = entity;
            this.entityClass = entity.getClass();
            this.genericType = null;
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMetadata(MultivaluedMap<String, Object> metadata) {
        this.metadata = new CaseInsensitiveMap<Object>();
        this.metadata.putAll(metadata);
    }
    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }

    public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns) {
        throw new IllegalStateException("Entity is not backed by an input stream");
    }

}
