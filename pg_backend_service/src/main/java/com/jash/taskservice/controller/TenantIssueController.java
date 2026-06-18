package com.jash.taskservice.controller;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import com.jash.taskservice.domain.model.IssueMaster;
import com.jash.taskservice.domain.model.UserMaster;
import com.jash.taskservice.repository.IssueMasterRepository;
import com.jash.taskservice.repository.UserMasterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tenant/issues")
public class TenantIssueController {

    private final IssueMasterRepository issueRepository;
    private final UserMasterRepository userRepository;

    public TenantIssueController(IssueMasterRepository issueRepository, UserMasterRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    // 1. Submit a fresh maintenance complaint ticket
    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN', 'OWNER')")
    public ResponseEntity<IssueMaster> raiseIssue(@RequestBody IssueMaster issue, Principal principal) {
        UserMaster user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new BusinessRuleException("Identity error: Profile not found."));

        if (user.getAssociatedPropertyId() == null) {
            throw new BusinessRuleException("Cannot raise issue: Tenant is not currently checked into any property.");
        }

        // Implicit extraction values to secure multi-tenant parameters
        issue.setReportedByUserId(user.getId());
        issue.setPropertyId(user.getAssociatedPropertyId());
        issue.setStatus("OPEN");
        issue.setCreatedAt(LocalDateTime.now());

        IssueMaster saved = issueRepository.save(issue);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 2. Fetch all historical complaints filed by this specific tenant
    @GetMapping("/my-tickets")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<IssueMaster>> getMyIssues(Principal principal) {
        UserMaster user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new BusinessRuleException("Identity error."));

        List<IssueMaster> records = issueRepository.findByReportedByUserId(user.getId());
        return ResponseEntity.ok(records);
    }
}