package com.smartoa.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartoa.common.BusinessException;
import com.smartoa.dto.LeaveSubmitRequest;
import com.smartoa.entity.ApprovalNode;
import com.smartoa.entity.ApprovalRecord;
import com.smartoa.entity.LeaveRequest;
import com.smartoa.entity.User;
import com.smartoa.mapper.ApprovalNodeMapper;
import com.smartoa.mapper.ApprovalRecordMapper;
import com.smartoa.mapper.LeaveRequestMapper;
import com.smartoa.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestMapper leaveRequestMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final ApprovalNodeMapper approvalNodeMapper;
    private final UserMapper userMapper;

    @Transactional
    public LeaveRequest submitLeave(Long applicantId, LeaveSubmitRequest dto) {
        User applicant = userMapper.selectById(applicantId);
        if (applicant == null) {
            throw new BusinessException("用户不存在");
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
        leaveRequestMapper.updateById(request);
        return request;
    }

    @Transactional
    public void approveLeave(Long requestId, Long approverId, String action, String comment) {
        LeaveRequest request = leaveRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("请假单不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException("该请假单已处理");
        }
        if (!approverId.equals(request.getCurrentApproverId())) {
            throw new BusinessException("您不是当前审批人");
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
     * 查找当前节点之后第一个满足条件的节点并解析审批人。
     * 节点条件表达式为 null/空 或 SpEL 评估为 true 时进入，
     * 否则跳过继续搜索下一个。返回 true 表示找到，false 表示流程结束。
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

        // 确定搜索起始位置
        int startIndex = 0;
        if (currentNodeId != null) {
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getId().equals(currentNodeId)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }

        // 从起始位置向后搜索，跳过条件不满足的节点
        for (int i = startIndex; i < nodes.size(); i++) {
            ApprovalNode node = nodes.get(i);
            if (evaluateCondition(node.getConditionExpression(), request)) {
                Long nextApproverId = resolveApprover(node, applicant);
                request.setCurrentNodeId(node.getId());
                request.setCurrentApproverId(nextApproverId);
                return true;
            }
        }

        return false; // 没有符合条件的下一节点
    }

    /**
     * 评估节点的 SpEL 条件表达式。null/空 → 始终进入。
     * 表达式异常时默认进入（fail-open），避免审批卡死。
     */
    /**
     * SpEL 条件变量载体 — 作为 root object，让表达式可直接用 days、leaveType 等字段名。
     */
    public record ConditionVars(String leaveType, long days,
                                 java.time.LocalDate startDate, java.time.LocalDate endDate) {}

    private boolean evaluateCondition(String expression, LeaveRequest request) {
        if (expression == null || expression.isBlank()) {
            return true;
        }
        try {
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            ConditionVars vars = new ConditionVars(
                    request.getLeaveType(), days,
                    request.getStartDate(), request.getEndDate());
            StandardEvaluationContext ctx = new StandardEvaluationContext(vars);
            Boolean result = new SpelExpressionParser()
                    .parseExpression(expression).getValue(ctx, Boolean.class);
            log.debug("condition eval: expr='{}' days={} leaveType={} => {}", expression, days, request.getLeaveType(), result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("condition eval error: expr='{}' — {}", expression, e.getMessage());
            return true;
        }
    }

    /**
     * 根据节点配置解析实际的审批人 ID。
     */
    private Long resolveApprover(ApprovalNode node, User applicant) {
        return switch (node.getApproverType()) {
            case "DIRECT_LEADER" -> applicant.getDirectLeaderId();
            case "DEPARTMENT_HEAD" -> applicant.getDepartmentHeadId();
            case "SPECIFIC_USER" -> node.getApproverId();
            default -> throw new BusinessException("不支持的审批人类型: " + node.getApproverType());
        };
    }

    // ========== 撤回 ==========

    @Transactional
    public void withdrawLeave(Long requestId, Long applicantId) {
        LeaveRequest request = leaveRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("请假单不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException("只能撤回审批中的申请");
        }
        if (!applicantId.equals(request.getApplicantId())) {
            throw new BusinessException("只能撤回自己的申请");
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveRequestId(requestId);
        record.setApproverId(applicantId);
        record.setAction("WITHDRAW");
        record.setApprovalStep(request.getApprovalStep());
        record.setNodeId(request.getCurrentNodeId());
        approvalRecordMapper.insert(record);

        request.setStatus("WITHDRAWN");
        request.setCurrentApproverId(null);
        request.setCurrentNodeId(null);
        leaveRequestMapper.updateById(request);
    }

    // ========== 转派 ==========

    @Transactional
    public void transferLeave(Long requestId, Long currentApproverId, Long toUserId) {
        LeaveRequest request = leaveRequestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("请假单不存在");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new BusinessException("只能转派审批中的申请");
        }
        if (!currentApproverId.equals(request.getCurrentApproverId())) {
            throw new BusinessException("您不是当前审批人，无法转派");
        }

        User target = userMapper.selectById(toUserId);
        if (target == null) {
            throw new BusinessException("目标用户不存在");
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveRequestId(requestId);
        record.setApproverId(currentApproverId);
        record.setAction("TRANSFER");
        record.setComment("转派给 " + target.getRealName());
        record.setApprovalStep(request.getApprovalStep());
        record.setNodeId(request.getCurrentNodeId());
        approvalRecordMapper.insert(record);

        request.setCurrentApproverId(toUserId);
        leaveRequestMapper.updateById(request);
    }

    // ========== 滞留修復 ==========

    /** 审批节点配置晚于数据提交时，已提交申请的 currentApproverId 为 null，无法审批。
     *  扫描所有 PENDING 且 currentApproverId 为 null 的申请，重新走一次节点分配。 */
    @Transactional
    public int repairStuckRequests() {
        List<LeaveRequest> stuck = leaveRequestMapper.selectList(
                new LambdaQueryWrapper<LeaveRequest>()
                        .eq(LeaveRequest::getStatus, "PENDING")
                        .isNull(LeaveRequest::getCurrentApproverId));

        for (LeaveRequest r : stuck) {
            User applicant = userMapper.selectById(r.getApplicantId());
            if (applicant == null) continue;
            r.setApprovalStep(0);
            r.setCurrentNodeId(null);
            advanceToNextNode(r, applicant);
            leaveRequestMapper.updateById(r);
        }
        return stuck.size();
    }

    // ========== 查询 ==========

    /** 管理画面用 — 全件取得（ロールによる制御は Controller で実施） */
    public List<LeaveRequest> getAllRequests() {
        return leaveRequestMapper.selectList(new LambdaQueryWrapper<LeaveRequest>()
                .orderByDesc(LeaveRequest::getCreateTime));
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
