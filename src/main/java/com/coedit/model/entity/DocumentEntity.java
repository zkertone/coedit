package com.coedit.model.entity;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "documents")
public class DocumentEntity {
    @Id 
    private String id;

    @Indexed(unique = true)
    private String title;

    private String content; // 存储原始内容或Yjs序列化数据

    private String creatorId; // 创建者ID

    private Instant createdAt; // 创建时间

    private Instant updatedAt; // 更新时间

    //权限管理(key:用户ID, value:权限类型)
    private Map<String, String> permissions = new HashMap<>(); // 存储用户ID和权限类型

    // 文档版本历史
    private List<DocumentVersion> versions = new ArrayList<>();

    //操作日志
    private List<DocumentOperationLog> operationLogs = new ArrayList<>();
}
