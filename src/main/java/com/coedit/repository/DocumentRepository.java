package com.coedit.repository;

import com.coedit.model.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
    List<DocumentEntity> findAllByCreatorId(String creatorId);
} 