package com.smartoa.repository;

import com.smartoa.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByLeaveRequestIdOrderByCreateTimeAsc(Long leaveRequestId);
}
