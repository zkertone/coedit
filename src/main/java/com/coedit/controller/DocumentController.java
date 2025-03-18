package com.coedit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.coedit.model.entity.DocumentEntity;
import com.coedit.service.intf.DocumentService;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    // 从认证上下文中获取用户ID
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        throw new IllegalStateException("用户未认证或认证信息无效");
    }

    //创建文档
    @PostMapping
    public ResponseEntity<DocumentEntity> createDocument(@RequestBody DocumentEntity document) {
        String userId = getCurrentUserId();
        document.setCreatorId(userId);
        DocumentEntity createdDocument = documentService.createDocument(document);
        return ResponseEntity.created(URI.create("/api/documents/" + createdDocument.getId())).body(createdDocument);
    }

    //获取文档
    @GetMapping("/{id}")
    public ResponseEntity<DocumentEntity> getDocument(@PathVariable String id) {
        String userId = getCurrentUserId();
        DocumentEntity document = documentService.getDocumentById(id, userId);
        return ResponseEntity.ok(document);
    }

    //更新文档
    @PutMapping("/{id}")
    public ResponseEntity<DocumentEntity> updateDocument(@PathVariable String id, @RequestBody DocumentEntity document) {
        String userId = getCurrentUserId();
        DocumentEntity updatedDocument = documentService.updateDocument(id, document.getContent(), userId);
        return ResponseEntity.ok(updatedDocument);
    }

    //删除文档
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        String userId = getCurrentUserId();
        documentService.deleteDocument(id, userId);
        return ResponseEntity.noContent().build();
    }

    //添加权限
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> addPermission(
            @PathVariable String id, 
            @RequestBody Map<String, String> permissionData) {
        String userId = getCurrentUserId();
        String targetUserId = permissionData.get("targetUserId");
        String role = permissionData.get("role");
        documentService.addPermission(id, targetUserId, role, userId);
        return ResponseEntity.noContent().build();
    }

    //删除权限
    @DeleteMapping("/{id}/permissions/{targetUserId}")
    public ResponseEntity<Void> deletePermission(
            @PathVariable String id, 
            @PathVariable String targetUserId) {
        String userId = getCurrentUserId();
        documentService.removePermission(id, targetUserId, userId);
        return ResponseEntity.noContent().build();
    }

    //查询全部
    @GetMapping
    public ResponseEntity<List<DocumentEntity>> fetchAllDocuments() {
        String userId = getCurrentUserId();
        List<DocumentEntity> documents = documentService.fetchAllDocuments(userId);
        return ResponseEntity.ok(documents);
    }   
}