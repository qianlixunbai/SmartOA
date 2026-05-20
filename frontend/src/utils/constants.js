export const LEAVE_TYPES = ['年假', '事假', '病假', '婚假', '产假', '调休']

export const STATUS_MAP = {
  PENDING: { label: '审批中', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已驳回', type: 'danger' }
}

export const ACTION_MAP = {
  APPROVE: { label: '通过', type: 'success' },
  REJECT: { label: '驳回', type: 'danger' }
}

export const STEP_MAP = {
  0: { label: '直属领导审批', icon: 'UserFilled' },
  1: { label: '部门总监审批', icon: 'Avatar' },
  2: { label: '审批完成', icon: 'CircleCheckFilled' }
}

export const STEP_LABELS = ['直属领导审批', '部门总监审批', '完成']
