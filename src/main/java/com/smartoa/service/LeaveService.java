package com.smartoa.service;

import com.smartoa.dto.LeaveSubmitDTO;
import com.smartoa.entity.ApprovalRecord;
import com.smartoa.entity.LeaveRequest;
import com.smartoa.entity.User;
import com.smartoa.repository.ApprovalRecordRepository;
import com.smartoa.repository.LeaveRequestRepository;
import com.smartoa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public LeaveRequest submitLeave(Long applicantId, LeaveSubmitDTO dto) {
        User applicant = userRepository.getReferenceById(applicantId);
        LeaveRequest request = new LeaveRequest();
        request.setApplicant(applicant);
        request.setLeaveType(dto.getLeaveType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus("PENDING");
        request.setApprovalStep(0);
        request.setCurrentApproverId(applicant.getDirectLeaderId());
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public void approveLeave(Long requestId, Long approverId, String action, String comment) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("请假单不存在"));
        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("该请假单已处理");
        }
        if (!approverId.equals(request.getCurrentApproverId())) {
            throw new RuntimeException("您不是当前审批人");
        }

        User approver = userRepository.getReferenceById(approverId);
        int currentStep = request.getApprovalStep();

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveRequest(request);
        record.setApprover(approver);
        record.setAction(action);
        record.setComment(comment);
        record.setApprovalStep(currentStep);
        approvalRecordRepository.save(record);

        if ("REJECT".equals(action)) {
            request.setStatus("REJECTED");
            request.setCurrentApproverId(null);
        } else {
            if (currentStep == 0) {
                request.setApprovalStep(1);
                request.setCurrentApproverId(request.getApplicant().getDepartmentHeadId());
            } else {
                request.setApprovalStep(2);
                request.setStatus("APPROVED");
                request.setCurrentApproverId(null);
            }
        }
        leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getMyRequests(Long applicantId) {
        return leaveRequestRepository.findByApplicantIdOrderByCreateTimeDesc(applicantId);
    }

    public List<LeaveRequest> getPendingRequests(Long approverId) {
        return leaveRequestRepository.findByCurrentApproverIdAndStatusOrderByCreateTimeDesc(approverId, "PENDING");
    }

    public List<LeaveRequest> getDoneRequests(Long approverId) {
        return leaveRequestRepository.findByCurrentApproverIdAndStatusNotOrderByCreateTimeDesc(approverId, "PENDING");
    }

    public LeaveRequest getRequestDetail(Long requestId) {
        return leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("请假单不存在"));
    }

    public List<ApprovalRecord> getApprovalRecords(Long requestId) {
        return approvalRecordRepository.findByLeaveRequestIdOrderByCreateTimeAsc(requestId);
    }
}
