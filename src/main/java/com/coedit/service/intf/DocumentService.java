package com.coedit.service.intf;

import com.coedit.model.entity.DocumentEntity;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public interface DocumentService {
  
    public DocumentEntity createDocument(DocumentEntity document);

    public DocumentEntity updateDocument(String documentId, String newContent, String userId);

    public void deleteDocument(String documentId, String userId);

    public DocumentEntity getDocumentById(String documentId, String userId);

    public void addPermission(String documentId, String targetUserId, String role, String operationId);

    public void removePermission(String documentId, String targetUserId, String operationId);
    
    //查询全部
    public List<DocumentEntity> fetchAllDocuments(String creatorId);
}
