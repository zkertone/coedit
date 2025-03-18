package com.coedit.service.impl;

import com.coedit.model.entity.DocumentEntity;
import com.coedit.repository.DocumentRepository;
import com.coedit.service.intf.DocumentService;
import com.coedit.exception.DocumentNotFoundException;
import com.coedit.exception.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public DocumentEntity createDocument(DocumentEntity document) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setTitle(document.getTitle());
        documentEntity.setCreatorId(document.getCreatorId());
        documentEntity.setCreatedAt(Instant.now());
        documentEntity.setUpdatedAt(Instant.now()); 
        documentEntity.getPermissions().put(document.getCreatorId(), "OWNER");
        return documentRepository.save(documentEntity);
    }

    @Override
    public DocumentEntity updateDocument(String documentId,String newContent, String userId) {
        DocumentEntity document = getDocumentById(documentId, userId);
        if (!hasEditPermission(document, userId)) {
            throw new PermissionDeniedException("仅所有者或编辑者可修改文档");
        }
        document.setContent(newContent);
        document.setUpdatedAt(Instant.now());
        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(String documentId, String userId) {
        DocumentEntity document = getDocumentById(documentId, userId);
        if (!isOwner(document, userId)) {
            throw new PermissionDeniedException("仅所有者可删除文档");
        }
        documentRepository.deleteById(documentId);
    }

    @Override
    public DocumentEntity getDocumentById(String documentId, String userId) {
        DocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("文档不存在"));
        if (!hasAccessPermission(document, userId)) {
            throw new PermissionDeniedException("无权访问此文档");
        }
        return document;
    }

    @Override
    public List<DocumentEntity> fetchAllDocuments(String creatorId) {
        return documentRepository.findAllByCreatorId(creatorId);
    }

    //权限管理
    @Override
    public void addPermission(String documentId, String targetUserId, String role,String operationId) {
        DocumentEntity document = getDocumentById(documentId, operationId);
        if (!isOwner(document, operationId)) {
            throw new PermissionDeniedException("仅所有者可添加权限");
        }
        document.getPermissions().put(targetUserId, role);
        documentRepository.save(document);
    }

    @Override
    public void removePermission(String documentId, String targetUserId,String operationId) {
        DocumentEntity document = getDocumentById(documentId, operationId);
        if (!isOwner(document, operationId)) {
            throw new PermissionDeniedException("仅所有者可删除权限");
        }
        document.getPermissions().remove(targetUserId);
        documentRepository.save(document);
    }

    @Override
    public Map<String, String> getDocumentPermissions(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("文档不存在"))
                .getPermissions();
    }
    //权限校验工具方法
    private boolean hasAccessPermission(DocumentEntity document, String userId) {
        return document.getPermissions().containsKey(userId) || document.getCreatorId().equals(userId);
    }

    private boolean hasEditPermission(DocumentEntity document, String userId) {
        String role = document.getPermissions().get(userId);
        return "OWNER".equals(role) || "EDITOR".equals(role);
    }
    
    private boolean isOwner(DocumentEntity document, String userId) {
        return "OWNER".equals(document.getPermissions().get(userId)) || document.getCreatorId().equals(userId);
    }
}