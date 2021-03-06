/* 
 * The MIT License
 *
 * Copyright 2013 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.acteur;

import com.mastfrog.acteur.headers.HeaderValueType;
import com.mastfrog.acteur.headers.Headers;
import com.mastfrog.acteur.headers.Method;
import static com.mastfrog.acteur.headers.Method.OPTIONS;
import com.mastfrog.acteur.preconditions.Description;
import com.mastfrog.acteur.preconditions.Methods;
import static com.mastfrog.acteur.server.ServerModule.DEFAULT_CORS_ALLOW_ORIGIN;
import static com.mastfrog.acteur.server.ServerModule.DEFAULT_CORS_MAX_AGE_MINUTES;
import static com.mastfrog.acteur.server.ServerModule.SETTINGS_KEY_CORS_ALLOW_ORIGIN;
import static com.mastfrog.acteur.server.ServerModule.SETTINGS_KEY_CORS_MAX_AGE_MINUTES;
import com.mastfrog.acteur.util.CacheControl;
import com.mastfrog.acteur.util.CacheControlTypes;
import com.mastfrog.settings.Settings;
import com.mastfrog.util.time.TimeUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;

/**
 *
 * @author Tim Boudreau
 */
@Description("Answers CORS preflight HTTP OPTIONS requests - see the ajax spec")
@Methods(OPTIONS)
final class CORSResource extends Page {

    private static final HeaderValueType<?>[] hdrs = new HeaderValueType<?>[]{
        Headers.CONTENT_TYPE,
        Headers.ACCEPT,
        Headers.X_REQUESTED_WITH
    };

    private static final Method[] methods = new Method[]{
        Method.GET,
        Method.POST,
        Method.PUT,
        Method.DELETE,
        Method.OPTIONS
    };

    @Inject
    CORSResource(ActeurFactory af) {
        add(CorsHeaders.class);
    }

    private static final class CorsHeaders extends Acteur {
        @Inject
        CorsHeaders(Settings settings) {
            String allowOrigin = settings.getString(SETTINGS_KEY_CORS_ALLOW_ORIGIN, DEFAULT_CORS_ALLOW_ORIGIN);
            Duration dur = Duration.of(
                    settings.getLong(SETTINGS_KEY_CORS_MAX_AGE_MINUTES, DEFAULT_CORS_MAX_AGE_MINUTES), ChronoUnit.MINUTES);
            add(Headers.CONTENT_LENGTH, 0);
            add(Headers.ACCESS_CONTROL_ALLOW_ORIGIN.toStringHeader(), allowOrigin);
            add(Headers.ACCESS_CONTROL_ALLOW, methods);
            add(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
            add(Headers.ACCESS_CONTROL_ALLOW_HEADERS, hdrs);
            add(Headers.ACCESS_CONTROL_MAX_AGE, dur);
            add(Headers.CACHE_CONTROL, CacheControl.$(CacheControlTypes.Public).add(CacheControlTypes.max_age, TimeUtil.years(1)));
            setState(new RespondWith(HttpResponseStatus.NO_CONTENT));
        }
    }
}
