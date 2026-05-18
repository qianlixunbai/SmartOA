package com.smartoa.repository;

import com.smartoa.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByApplicantIdOrderByCreateTimeDesc(Long applicantId);

    List<LeaveRequest> findByStatusOrderByCreateTimeDesc(String status);

    List<LeaveRequest> findByStatusNotOrderByCreateTimeDesc(String status);
}
