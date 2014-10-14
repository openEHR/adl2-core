/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
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

package org.openehr.adl.parser;

import com.google.common.base.Charsets;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * An InputStream Reader wrapper that supports Windows BOM headers.
 * If the stream begins with a BOM header, skip the header and force UTF-8 charset, otherwise use default charset.
 *
 * @author markopi
 */
public class BomSupportingReader extends Reader {
    private static final byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private final Reader reader;

    public BomSupportingReader(InputStream in, Charset defaultCharset) throws IOException {
        final InputStream bin = in.markSupported() ? in : new BufferedInputStream(in);
        bin.mark(3);

        byte[] buffer = new byte[3];
        int count = bin.read(buffer);
        if (count == 3 && Arrays.equals(BOM, buffer)) {
            reader = new InputStreamReader(bin, Charsets.UTF_8);
        } else {
            bin.reset();
            reader = new InputStreamReader(bin, defaultCharset);
        }

    }

    public BomSupportingReader(Path path, Charset defaultCharset) throws IOException {
        this(new FileInputStream(path.toFile()), defaultCharset);
    }


    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
