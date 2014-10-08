/*
 * Copyright (C) 2014 Marand
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
