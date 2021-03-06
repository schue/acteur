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
package com.mastfrog.acteur.headers;

import com.mastfrog.util.Checks;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;

/**
 *
 * @author Tim Boudreau
 */
final class CookieHeaderNetty428 extends AbstractHeader<Cookie[]> {

    private static final Cookie[] EMPTY = new Cookie[0];
    private final boolean strict;

    CookieHeaderNetty428(boolean strict) {
        super(Cookie[].class, HttpHeaderNames.COOKIE);
        this.strict = strict;
    }

    @Override
    public String toString(Cookie[] value) {
        Checks.notNull("value", value);
        return strict ? ClientCookieEncoder.STRICT.encode(value) : ClientCookieEncoder.LAX.encode(value);
    }

    @Override
    public Cookie[] toValue(CharSequence value) {
        Checks.notNull("value", value);
        Cookie result = strict ? ClientCookieDecoder.STRICT.decode(value.toString()) : ClientCookieDecoder.LAX.decode(value.toString());
        return result == null ? EMPTY : new Cookie[]{result};
    }

}
