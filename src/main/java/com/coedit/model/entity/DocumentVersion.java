package com.coedit.model.entity;

import lombok.Data;

@Data
public class DocumentVersion {
    private String versionId;
    private String content;
    private String userId;    // 修改者ID
    private String timestamp; // 修改时间
    private String comment;   // 版本说明
} 