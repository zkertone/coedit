package com.coedit.service.impl;

import com.coedit.model.entity.DocumentEntity;
import com.coedit.repository.DocumentRepository;
import com.coedit.service.intf.DocumentService;
import com.coedit.exception.DocumentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public DocumentEntity createDocument(DocumentEntity document) {
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setTitle(document.getTitle());
        documentEntity.setCreatorId(document.getCreatorId());
        documentEntity.setCreatedAt(Instant.now().toString());
        documentEntity.setUpdatedAt(Instant.now().toString());
        documentEntity.getPermissions().put(document.getCreatorId(), "owner");
        return documentRepository.save(documentEntity);
    }

    @Override
    public DocumentEntity updateDocument(String documentId, DocumentEntity document) {
        document.setId(documentId);
        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(String documentId) {
        documentRepository.deleteById(documentId);
    }

    @Override
    public DocumentEntity getDocumentById(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("文档不存在"));
    }

    //权限管理
    @Override
    public void addPermission(String documentId, String userId, String permission) {
        DocumentEntity document = getDocumentById(documentId);
        document.getPermissions().put(userId, permission);
        documentRepository.save(document);
    }

    @Override
    public void removePermission(String documentId, String userId) {
        DocumentEntity document = getDocumentById(documentId);
        document.getPermissions().remove(userId);
        documentRepository.save(document);
    }

    @Override
    public List<String> getPermissions(String documentId) {
        DocumentEntity document = getDocumentById(documentId);
        return new ArrayList<>(document.getPermissions().keySet());
    }

    //权限校验工具方法
    @Override
    public boolean hasAccessPermission(DocumentEntity document, String userId) {
        return document.getPermissions().containsKey(userId) || document.getCreatorId().equals(userId);
    }

    @Override
    public boolean hasEditPermission(DocumentEntity document, String userId) {
        String role = document.getPermissions().get(userId);
        return "OWNER".equals(role) || "EDITOR".equals(role);
    }
    
    
}