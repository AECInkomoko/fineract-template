/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.documentmanagement.contentrepository;

import java.io.BufferedInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.config.FineractProperties;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.documentmanagement.exception.ContentManagementException;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileSystemContentPathSanitizer implements ContentPathSanitizer {

    private static final Logger log = LoggerFactory.getLogger(FileSystemContentPathSanitizer.class);

    private final FineractProperties fineractProperties;

    private List<Pattern> regexWhitelist;

    private static Pattern OVERWRITE_SIBLING_IMAGE = Pattern.compile(".*\\.\\./+[0-9]+/+.*");

    @Autowired
    public FileSystemContentPathSanitizer(final FineractProperties fineractProperties) {
        this.fineractProperties = fineractProperties;
    }

    @PostConstruct
    public void init() {
        regexWhitelist = fineractProperties.getContent().getRegexWhitelist().stream().map(Pattern::compile).toList();
    }

    @Override
    public String sanitize(String path) {
        return sanitize(path, null);
    }

    @Override
    public String sanitize(String path, BufferedInputStream is) {
        try {
            if (OVERWRITE_SIBLING_IMAGE.matcher(path).matches()) {
                throw new RuntimeException(String.format("Trying to overwrite another resource's image: %s", path));
            }

            String sanitizedPath = Path.of(path).normalize().toString();

            String fileName = FilenameUtils.getName(sanitizedPath).toLowerCase();

            if (log.isDebugEnabled()) {
                log.debug("Path: {} -> {} ({})", path, sanitizedPath, fileName);
            }

            if (fineractProperties.getContent().isRegexWhitelistEnabled()) {
                boolean matches = regexWhitelist.stream().anyMatch(p -> p.matcher(fileName).matches());

                if (!matches) {
                    throw new RuntimeException(String.format("File type not allowed: %s", fileName));
                }
            }

            if (is != null && fineractProperties.getContent().isMimeWhitelistEnabled()) {
                Tika tika = new Tika();
                String extensionMimeType = tika.detect(fileName);

                if (StringUtils.isEmpty(extensionMimeType)) {
                    throw new RuntimeException(String.format("Could not detect mime type for filename %s!", fileName));
                }

                if (!fineractProperties.getContent().getMimeWhitelist().contains(extensionMimeType)) {
                    throw new RuntimeException(
                            String.format("Detected mime type %s for filename %s not allowed!", extensionMimeType, fileName));
                }

                String contentMimeType = extensionMimeType;

                if (StringUtils.isEmpty(contentMimeType)) {
                    throw new RuntimeException(String.format("Could not detect content mime type for %s!", fileName));
                }

                if (!fineractProperties.getContent().getMimeWhitelist().contains(contentMimeType)) {
                    throw new RuntimeException(
                            String.format("Detected content mime type %s for %s not allowed!", contentMimeType, fileName));
                }

                if (!contentMimeType.equalsIgnoreCase(extensionMimeType)) {
                    throw new RuntimeException(String.format("Detected filename (%s) and content (%s) mime type do not match!",
                            extensionMimeType, contentMimeType));
                }
            }

            Path target = Path.of(sanitizedPath);
            Path rootFolder = Path.of(fineractProperties.getContent().getFilesystem().getRootFolder(),
                    ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim());

            if (!target.startsWith(rootFolder)) {
                throw new RuntimeException(String.format("Path traversal attempt: %s (%s)", target, rootFolder));
            }

            return sanitizedPath;
        } catch (Exception e) {
            throw new ContentManagementException(path, e.getMessage(), e);
        }
    }
}
