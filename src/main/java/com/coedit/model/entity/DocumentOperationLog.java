package com.coedit.model.entity;

import lombok.Data;

@Data
public class DocumentOperationLog {
    private String operationId;
    private String userId;      // 操作者ID
    private String operationType; // 操作类型（如：编辑、查看、分享等）
    private String timestamp;    // 操作时间
    private String details;      // 操作详情
} 