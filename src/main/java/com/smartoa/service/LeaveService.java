package com.smartoa.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartoa.dto.LeaveSubmitDTO;
import com.smartoa.entity.ApprovalRecord;
import com.smartoa.entity.LeaveRequest;
import com.smartoa.entity.User;
import com.smartoa.mapper.ApprovalRecordMapper;
import com.smartoa.mapper.LeaveRequestMapper;
import com.smartoa.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final UserMapper userMapper;

    @Transactional
    public LeaveRequest submitLeave(Long applicantId, LeaveSubmitDTO dto) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("用户不存在");
        }
        LeaveRequest request = new LeaveRequest();
        request.setApplicantId(applicantId);
        request.setLeaveType(dto.getLeaveType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus("PENDING");
        request.setApprovalStep(0);
        request.setCurrentApproverId(applicant.getDirectLeaderId());
        leaveRequestMapper.insert(request);
        return request;
    }

    @Transactional
    public void approveLeave(Long requestId, Long approverId, String action, String comment) {
        LeaveRequest request = leaveRequestMapper.selectById(requestId);
        if (request == null) {
            throw new RuntimeException("请假单不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("该请假单已处理");
        }
        if (!approverId.equals(request.getCurrentApproverId())) {
            throw new RuntimeException("您不是当前审批人");
        }

        User approver = userMapper.selectById(approverId);
        int currentStep = request.getApprovalStep();

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveRequestId(requestId);
        record.setApproverId(approverId);
        record.setAction(action);
        record.setComment(comment);
        record.setApprovalStep(currentStep);
        approvalRecordMapper.insert(record);

        if ("REJECT".equals(action)) {
            request.setStatus("REJECTED");
            request.setCurrentApproverId(null);
        } else {
            if (currentStep == 0) {
                request.setApprovalStep(1);
                User applicant = userMapper.selectById(request.getApplicantId());
                request.setCurrentApproverId(applicant.getDepartmentHeadId());
            } else {
                request.setApprovalStep(2);
                request.setStatus("APPROVED");
                request.setCurrentApproverId(null);
            }
        }
        leaveRequestMapper.updateById(request);
    }

    public List<LeaveRequest> getMyRequests(Long applicantId) {
        return leaveRequestMapper.selectList(new LambdaQueryWrapper<LeaveRequest>()
                .eq(LeaveRequest::getApplicantId, applicantId)
                .orderByDesc(LeaveRequest::getCreateTime));
    }

    public List<LeaveRequest> getPendingRequests(Long approverId) {
        return leaveRequestMapper.selectList(new LambdaQueryWrapper<LeaveRequest>()
                .eq(LeaveRequest::getCurrentApproverId, approverId)
                .eq(LeaveRequest::getStatus, "PENDING")
                .orderByDesc(LeaveRequest::getCreateTime));
    }

    public List<LeaveRequest> getDoneRequests(Long approverId) {
        return leaveRequestMapper.selectList(new LambdaQueryWrapper<LeaveRequest>()
                .eq(LeaveRequest::getCurrentApproverId, approverId)
                .ne(LeaveRequest::getStatus, "PENDING")
                .orderByDesc(LeaveRequest::getCreateTime));
    }

    public LeaveRequest getRequestDetail(Long requestId) {
        return leaveRequestMapper.selectById(requestId);
    }

    public List<ApprovalRecord> getApprovalRecords(Long requestId) {
        return approvalRecordMapper.selectList(new LambdaQueryWrapper<ApprovalRecord>()
                .eq(ApprovalRecord::getLeaveRequestId, requestId)
                .orderByAsc(ApprovalRecord::getCreateTime));
    }
}
