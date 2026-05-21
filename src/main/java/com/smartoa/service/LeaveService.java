package com.smartoa.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartoa.dto.LeaveSubmitDTO;
import com.smartoa.entity.ApprovalNode;
import com.smartoa.entity.ApprovalRecord;
import com.smartoa.entity.LeaveRequest;
import com.smartoa.entity.User;
import com.smartoa.mapper.ApprovalNodeMapper;
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
    private final ApprovalNodeMapper approvalNodeMapper;
    private final UserMapper userMapper;

    @Transactional
    public LeaveRequest submitLeave(Long applicantId, LeaveSubmitDTO dto) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new RuntimeException("用户不存在");
        }

        LeaveRequest request = new LeaveRequest();
        request.setApplicantId(applicantId);
        request.setTemplateId(dto.getTemplateId());
        request.setLeaveType(dto.getLeaveType());
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus("PENDING");
        request.setApprovalStep(0);
        leaveRequestMapper.insert(request);

        // 动态读取模板的审批节点，找到第一个节点并解析审批人
        advanceToNextNode(request, applicant);
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

        int currentStep = request.getApprovalStep();

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveRequestId(requestId);
        record.setApproverId(approverId);
        record.setAction(action);
        record.setComment(comment);
        record.setApprovalStep(currentStep);
        record.setNodeId(request.getCurrentNodeId());
        approvalRecordMapper.insert(record);

        if ("REJECT".equals(action)) {
            request.setStatus("REJECTED");
            request.setCurrentApproverId(null);
            request.setCurrentNodeId(null);
            leaveRequestMapper.updateById(request);
            return;
        }

        // 动态找下一节点
        request.setApprovalStep(currentStep + 1);
        User applicant = userMapper.selectById(request.getApplicantId());
        boolean hasNext = advanceToNextNode(request, applicant);

        if (!hasNext) {
            request.setStatus("APPROVED");
            request.setCurrentApproverId(null);
            request.setCurrentNodeId(null);
        }
        leaveRequestMapper.updateById(request);
    }

    /**
     * 查找当前节点的下一个顺序节点并解析审批人。
     * 返回 true 表示找到下一节点，false 表示审批流程结束。
     */
    private boolean advanceToNextNode(LeaveRequest request, User applicant) {
        Long currentNodeId = request.getCurrentNodeId();
        Long templateId = request.getTemplateId();

        List<ApprovalNode> nodes = approvalNodeMapper.selectList(
                new LambdaQueryWrapper<ApprovalNode>()
                        .eq(ApprovalNode::getTemplateId, templateId)
                        .orderByAsc(ApprovalNode::getSortOrder));

        if (nodes.isEmpty()) {
            return false;
        }

        ApprovalNode nextNode;
        if (currentNodeId == null) {
            // 刚提交，取第一个节点
            nextNode = nodes.get(0);
        } else {
            // 找当前节点之后的节点
            int currentIndex = -1;
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getId().equals(currentNodeId)) {
                    currentIndex = i;
                    break;
                }
            }
            if (currentIndex < 0 || currentIndex >= nodes.size() - 1) {
                return false; // 没有下一节点了
            }
            nextNode = nodes.get(currentIndex + 1);
        }

        Long nextApproverId = resolveApprover(nextNode, applicant);
        request.setCurrentNodeId(nextNode.getId());
        request.setCurrentApproverId(nextApproverId);
        return true;
    }

    /**
     * 根据节点配置解析实际的审批人 ID。
     */
    private Long resolveApprover(ApprovalNode node, User applicant) {
        return switch (node.getApproverType()) {
            case "DIRECT_LEADER" -> applicant.getDirectLeaderId();
            case "DEPARTMENT_HEAD" -> applicant.getDepartmentHeadId();
            case "SPECIFIC_USER" -> node.getApproverId();
            default -> throw new RuntimeException("不支持的审批人类型: " + node.getApproverType());
        };
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
