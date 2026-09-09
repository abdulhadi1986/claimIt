package com.foundIt.claimIt.domain.type;

import org.apache.commons.lang3.StringUtils;

public enum FileType {
    PDF(".pdf"),
    DOCX(".docx"),
    TXT(".txt");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public static FileType getFileTypeFromExtension(String fileExtension) {
        if (StringUtils.isBlank(fileExtension)) {
            return null;
        }

        for (FileType fileType : FileType.values()) {
            if (fileType.extension.equalsIgnoreCase(fileExtension.trim())) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("FileType is not supported: " + fileExtension);
    }
}
