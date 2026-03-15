package com.civicfix.repository;

import com.civicfix.model.Issue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IssueRepository extends MongoRepository<Issue, String> {

    List<Issue> findByStatus(String status);

    List<Issue> findByCategory(String category);

    List<Issue> findBySeverity(String severity);

    List<Issue> findAllByOrderByUpvotesDesc();

    long countByStatus(String status);

    long countBySeverity(String severity);
}
