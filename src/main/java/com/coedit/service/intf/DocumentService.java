package com.coedit.service.intf;

import com.coedit.model.entity.DocumentEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface DocumentService {
  
    public DocumentEntity createDocument(DocumentEntity document);

    public DocumentEntity updateDocument(String documentId, DocumentEntity document);

    public void deleteDocument(String documentId);

    public DocumentEntity getDocumentById(String documentId);

    public void addPermission(String documentId, String userId, String permission);

    public void removePermission(String documentId, String userId);

    public List<String> getPermissions(String documentId);

    public boolean checkAccessPermission(String documentId, String userId);
    
    public boolean checkEditPermission(String documentId, String userId);
}
