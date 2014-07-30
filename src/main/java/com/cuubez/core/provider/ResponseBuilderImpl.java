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
import com.cuubez.core.util.HeaderHelper;
import com.cuubez.core.util.HttpHeaderNames;

import javax.ws.rs.core.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;


public class ResponseBuilderImpl extends Response.ResponseBuilder {

    protected Object entity;
    protected int status = -1;
    protected CaseInsensitiveMap<Object> metadata = new CaseInsensitiveMap<Object>();

    @Override
    public Response build() {
        if (status == -1 && entity == null) status = 204;
        else if (status == -1) status = 200;
        return new BuiltResponse(status, metadata, entity);
    }

    @Override
    public Response.ResponseBuilder clone() {
        ResponseBuilderImpl impl = new ResponseBuilderImpl();
        impl.metadata.putAll(metadata);
        impl.entity = entity;
        impl.status = status;
        return impl;
    }

    @Override
    public Response.ResponseBuilder status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public Response.ResponseBuilder entity(Object entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Response.ResponseBuilder type(MediaType type) {
        if (type == null) {
            metadata.remove(HttpHeaderNames.CONTENT_TYPE);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
        return this;
    }

    @Override
    public Response.ResponseBuilder type(String type) {
        if (type == null) {
            metadata.remove(HttpHeaderNames.CONTENT_TYPE);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
        return this;
    }

    @Override
    public Response.ResponseBuilder variant(Variant variant) {
        if (variant == null) {
            type((String) null);
            language((String) null);
            metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
            return this;
        }
        type(variant.getMediaType());
        language(variant.getLanguage());
        if (variant.getEncoding() != null) metadata.putSingle(HttpHeaderNames.CONTENT_ENCODING, variant.getEncoding());
        else metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
        return this;
    }

    @Override
    public Response.ResponseBuilder variants(List<Variant> variants) {
        if (variants == null) {
            metadata.remove(HttpHeaderNames.VARY);
            return this;
        }
        String vary = createVaryHeader(variants);
        metadata.putSingle(HttpHeaderNames.VARY, vary);

        return this;
    }

    public static String createVaryHeader(List<Variant> variants) {
        boolean accept = false;
        boolean acceptLanguage = false;
        boolean acceptEncoding = false;

        for (Variant variant : variants) {
            if (variant.getMediaType() != null) accept = true;
            if (variant.getLanguage() != null) acceptLanguage = true;
            if (variant.getEncoding() != null) acceptEncoding = true;
        }

        String vary = null;
        if (accept) vary = HttpHeaderNames.ACCEPT;
        if (acceptLanguage) {
            if (vary == null) vary = HttpHeaderNames.ACCEPT_LANGUAGE;
            else vary += ", " + HttpHeaderNames.ACCEPT_LANGUAGE;
        }
        if (acceptEncoding) {
            if (vary == null) vary = HttpHeaderNames.ACCEPT_ENCODING;
            else vary += ", " + HttpHeaderNames.ACCEPT_ENCODING;
        }
        return vary;
    }

    @Override
    public Response.ResponseBuilder language(String language) {
        if (language == null) {
            metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
        return this;
    }

    @Override
    public Response.ResponseBuilder location(URI location) {
        if (location == null) {
            metadata.remove(HttpHeaderNames.LOCATION);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.LOCATION, location);
        return this;
    }

    @Override
    public Response.ResponseBuilder contentLocation(URI location) {
        if (location == null) {
            metadata.remove(HttpHeaderNames.CONTENT_LOCATION);
            return this;
        }

        metadata.putSingle(HttpHeaderNames.CONTENT_LOCATION, location);
        return this;
    }

    @Override
    public Response.ResponseBuilder tag(EntityTag tag) {
        if (tag == null) {
            metadata.remove(HttpHeaderNames.ETAG);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.ETAG, tag);
        return this;
    }

    @Override
    public Response.ResponseBuilder tag(String tag) {
        if (tag == null) {
            metadata.remove(HttpHeaderNames.ETAG);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.ETAG, tag);
        return this;
    }

    @Override
    public Response.ResponseBuilder lastModified(Date lastModified) {
        if (lastModified == null) {
            metadata.remove(HttpHeaderNames.LAST_MODIFIED);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.LAST_MODIFIED, lastModified);
        return this;
    }

    @Override
    public Response.ResponseBuilder cacheControl(CacheControl cacheControl) {
        if (cacheControl == null) {
            metadata.remove(HttpHeaderNames.CACHE_CONTROL);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.CACHE_CONTROL, cacheControl);
        return this;
    }

    @Override
    public Response.ResponseBuilder header(String name, Object value) {
        if (value == null) {
            metadata.remove(name);
            return this;
        }
        metadata.add(name, value);
        return this;
    }

    @Override
    public Response.ResponseBuilder cookie(NewCookie... cookies) {
        if (cookies == null) {
            metadata.remove(HttpHeaderNames.SET_COOKIE);
            return this;
        }
        for (NewCookie cookie : cookies) {
            metadata.add(HttpHeaderNames.SET_COOKIE, cookie);
        }
        return this;
    }

    public Response.ResponseBuilder language(Locale language) {
        if (language == null) {
            metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
        return this;
    }

    public static SimpleDateFormat getDateFormatRFC822() {
        SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatRFC822;
    }

    public Response.ResponseBuilder expires(Date expires) {
        if (expires == null) {
            metadata.remove(HttpHeaderNames.EXPIRES);
            return this;
        }
        metadata.putSingle(HttpHeaderNames.EXPIRES, getDateFormatRFC822().format(expires));
        return this;
    }


    public Response.ResponseBuilder allow(String... methods) {
        if (methods == null) {
            return allow((Set<String>) null);
        }
        HashSet<String> set = new HashSet<String>();
        for (String m : methods) set.add(m);
        return allow(set);
    }

    public Response.ResponseBuilder allow(Set<String> methods) {
        HeaderHelper.setAllow(this.metadata, methods);
        return this;
    }

}
