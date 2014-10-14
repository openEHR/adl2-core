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

package org.openehr.adl.rm;

import com.google.common.base.Splitter;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author markopi
 * @since 24.7.2013
 */
public final class RmPath {
    private static final Pattern SEGMENT_PATTERN = Pattern.compile("([a-zA-Z0-9_]*)(\\[((ac|at|id)[0-9.]+)\\])?");

    @SuppressWarnings("ConstantConditions")
    public static final RmPath ROOT = new RmPath(null, "", null);

    public static final Splitter PATH_SPLITTER = Splitter.on("/").omitEmptyStrings();

    private final RmPath parent;
    private final String attribute;
    private final String nodeId;

    private RmPath(RmPath parent, String attribute, @Nullable String nodeId) {
        this.parent = parent;
        this.attribute = attribute;
        this.nodeId = nodeId;
    }

    public RmPath getParent() {
        return parent;
    }

    public String getAttribute() {
        return attribute;
    }

    @Nullable
    public String getNodeId() {
        return nodeId;
    }


    public RmPath resolve(String path) {
        List<Segment> segments = parseSegments(path);
        RmPath node = this;
        for (Segment segment : segments) {
            node = new RmPath(node, segment.attribute, segment.nodeId);
        }
        return node;
    }

    public RmPath resolve(String attribute, @Nullable String nodeId) {
        return new RmPath(this, attribute, nodeId);
    }

    public RmPath constrain(@Nullable String nodeId) {
        checkState(this.nodeId == null || nodeId == null, "Path already constrained");

        if (ObjectUtils.equals(this.nodeId, nodeId)) return this;
        return new RmPath(parent, attribute, nodeId);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RmPath rmPath = (RmPath) o;

        if (!attribute.equals(rmPath.attribute)) return false;
        if (nodeId != null ? !nodeId.equals(rmPath.nodeId) : rmPath.nodeId != null) return false;
        if (parent != null ? !parent.equals(rmPath.parent) : rmPath.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + attribute.hashCode();
        result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
        return result;
    }

    private void buildToString(StringBuilder builder) {
        if (parent != null) {
            parent.buildToString(builder);
            builder.append("/");
        }
        builder.append(attribute);
        if (nodeId != null) {
            builder.append("[").append(nodeId).append("]");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        buildToString(builder);
        return builder.toString();
    }

    public String toSegmentString() {
        StringBuilder builder = new StringBuilder();
        builder.append(attribute);
        if (nodeId != null) {
            builder.append("[").append(nodeId).append("]");
        }

        return builder.toString();
    }

    private static List<Segment> parseSegments(String origPathStr) {
        String pathStr = origPathStr;
        if (!pathStr.startsWith("/")) {
            pathStr = pathStr + "/";
        }

        List<Segment> result = new ArrayList<>();
        for (String segmentStr : PATH_SPLITTER.split(pathStr)) {
            Matcher m = SEGMENT_PATTERN.matcher(segmentStr);
            if (!m.matches()) {
                throw new IllegalArgumentException("Bad aql path: " + origPathStr);
            }
            Segment segment = new Segment();
            segment.attribute = m.group(1);
            segment.nodeId = m.groupCount() >= 3 ? m.group(3) : null;
            result.add(segment);
        }
        return result;
    }

    public static RmPath valueOf(String str) {
        if (str.startsWith("/")) {
            str = str.substring(1);
        }

        RmPath path = ROOT;
        List<Segment> segments = parseSegments(str);
        for (Segment segment : segments) {
            path = new RmPath(path, segment.attribute, segment.nodeId);
        }
        return path;
    }

    public List<RmPath> segments() {
        List<RmPath> result = new ArrayList<>();
        RmPath path = this;
        while (path != null && path.getParent() != null) {
            result.add(path);
            path = path.getParent();
        }
        Collections.reverse(result);
        return result;
    }

    private static class Segment {
        String attribute;
        String nodeId;
    }
}
