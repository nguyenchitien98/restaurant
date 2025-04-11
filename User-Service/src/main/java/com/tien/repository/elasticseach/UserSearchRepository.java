package com.tien.repository.elasticseach;

import com.tien.model.elasticsearch.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, Long> {
    List<UserDocument> findByUsernameContainingIgnoreCase(String username);
}
