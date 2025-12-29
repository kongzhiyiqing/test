package com.healthcare.service;

import com.healthcare.model.Referral;
import com.healthcare.util.HealthcareException;

import java.time.LocalDate;
import java.util.List;

/**
 * 转诊服务
 * 处理转诊相关的复杂业务逻辑
 *
 * @author Healthcare System
 * @version 1.0
 */
public class ReferralService extends BaseService {

    @Override
    public void initialize() throws HealthcareException {
        logServiceEvent("正在初始化转诊服务...");
        // 验证必要的Repository
        validateParameter(repositoryManager.getReferralRepository(), "ReferralRepository");
        validateParameter(repositoryManager.getPatientRepository(), "PatientRepository");
        validateParameter(repositoryManager.getClinicianRepository(), "ClinicianRepository");
        validateParameter(repositoryManager.getFacilityRepository(), "FacilityRepository");
        validateParameter(repositoryManager.getAppointmentRepository(), "AppointmentRepository");
        initialized = true;
        logServiceEvent("转诊服务初始化完成");
    }

    /**
     * 创建转诊
     */
    public Referral createReferral(String patientId, String referringClinicianId, String referredToClinicianId,
                                 String referringFacilityId, String referredToFacilityId, String urgencyLevel,
                                 String referralReason, String clinicalSummary, String requestedInvestigations)
            throws HealthcareException {
        validateInitialized();
        validateStringParameter(patientId, "patientId");
        validateStringParameter(referringClinicianId, "referringClinicianId");
        validateStringParameter(referredToClinicianId, "referredToClinicianId");
        validateStringParameter(referringFacilityId, "referringFacilityId");
        validateStringParameter(referredToFacilityId, "referredToFacilityId");

        return executeOperation("createReferral", () -> {
            try {
                // 验证所有相关实体存在
                validateReferralEntities(patientId, referringClinicianId, referredToClinicianId,
                                       referringFacilityId, referredToFacilityId);

                // 生成转诊ID
                String referralId = generateReferralId();

                // 创建转诊
                Referral referral = new Referral(referralId, patientId, referringClinicianId,
                                               referredToClinicianId, referringFacilityId, referredToFacilityId,
                                               LocalDate.now(), urgencyLevel, referralReason, clinicalSummary,
                                               requestedInvestigations, "New", null, null,
                                               LocalDate.now(), LocalDate.now());

                // 验证转诊数据
                validateReferralData(referral);

                // 保存转诊
                repositoryManager.getReferralRepository().save(referral);
                repositoryManager.flushAllData();

                logServiceEvent("转诊创建成功: " + referralId);
                return referral;

            } catch (HealthcareException e) {
                logServiceEvent("转诊创建失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 开始处理转诊
     */
    public void startReferralProcessing(String referralId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(referralId, "referralId");

        recordOperation("startReferralProcessing", () -> {
            try {
                Optional<Referral> referralOpt = repositoryManager.getReferralRepository().findById(referralId);
                if (!referralOpt.isPresent()) {
                    throw new HealthcareException("转诊不存在: " + referralId);
                }

                Referral referral = referralOpt.get();

                // 验证状态
                validateBusinessRule("New".equals(referral.getStatus()), "只能开始处理新转诊: " + referralId);

                referral.setStatus("In Progress");
                referral.setLastUpdated(LocalDate.now());

                repositoryManager.getReferralRepository().save(referral);
                repositoryManager.flushAllData();

                logServiceEvent("转诊开始处理: " + referralId);

            } catch (HealthcareException e) {
                logServiceEvent("开始处理转诊失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 完成转诊
     */
    public void completeReferral(String referralId, String outcome, String notes) throws HealthcareException {
        validateInitialized();
        validateStringParameter(referralId, "referralId");

        recordOperation("completeReferral", () -> {
            try {
                Optional<Referral> referralOpt = repositoryManager.getReferralRepository().findById(referralId);
                if (!referralOpt.isPresent()) {
                    throw new HealthcareException("转诊不存在: " + referralId);
                }

                Referral referral = referralOpt.get();

                // 验证状态
                validateBusinessRule(!"Completed".equals(referral.getStatus()), "转诊已完成: " + referralId);

                referral.setStatus("Completed");
                referral.setNotes((outcome != null ? "结果: " + outcome + "; " : "") +
                                (notes != null ? notes : ""));
                referral.setLastUpdated(LocalDate.now());

                repositoryManager.getReferralRepository().save(referral);
                repositoryManager.flushAllData();

                logServiceEvent("转诊完成: " + referralId);

            } catch (HealthcareException e) {
                logServiceEvent("完成转诊失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 关联预约到转诊
     */
    public void linkAppointmentToReferral(String referralId, String appointmentId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(referralId, "referralId");
        validateStringParameter(appointmentId, "appointmentId");

        recordOperation("linkAppointmentToReferral", () -> {
            try {
                // 验证转诊和预约存在
                Optional<Referral> referralOpt = repositoryManager.getReferralRepository().findById(referralId);
                if (!referralOpt.isPresent()) {
                    throw new HealthcareException("转诊不存在: " + referralId);
                }

                validateBusinessRule(repositoryManager.getAppointmentRepository().existsById(appointmentId),
                                   "预约不存在: " + appointmentId);

                Referral referral = referralOpt.get();
                referral.setAppointmentId(appointmentId);
                referral.setLastUpdated(LocalDate.now());

                repositoryManager.getReferralRepository().save(referral);
                repositoryManager.flushAllData();

                logServiceEvent("预约关联到转诊成功: " + appointmentId + " -> " + referralId);

            } catch (HealthcareException e) {
                logServiceEvent("关联预约到转诊失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 转诊升级（提高优先级）
     */
    public void escalateReferral(String referralId, String newUrgencyLevel, String reason) throws HealthcareException {
        validateInitialized();
        validateStringParameter(referralId, "referralId");
        validateStringParameter(newUrgencyLevel, "newUrgencyLevel");

        recordOperation("escalateReferral", () -> {
            try {
                Optional<Referral> referralOpt = repositoryManager.getReferralRepository().findById(referralId);
                if (!referralOpt.isPresent()) {
                    throw new HealthcareException("转诊不存在: " + referralId);
                }

                Referral referral = referralOpt.get();

                // 验证升级合理性
                validateBusinessRule(isHigherUrgency(newUrgencyLevel, referral.getUrgencyLevel()),
                                   "新优先级必须高于当前优先级");

                referral.setUrgencyLevel(newUrgencyLevel);
                referral.setNotes((referral.getNotes() != null ? referral.getNotes() + "; " : "") +
                                "优先级升级: " + reason);
                referral.setLastUpdated(LocalDate.now());

                repositoryManager.getReferralRepository().save(referral);
                repositoryManager.flushAllData();

                logServiceEvent("转诊优先级升级: " + referralId + " -> " + newUrgencyLevel);

            } catch (HealthcareException e) {
                logServiceEvent("转诊升级失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 获取转诊工作流
     */
    public ReferralWorkflow getReferralWorkflow(String referralId) throws HealthcareException {
        validateInitialized();
        validateStringParameter(referralId, "referralId");

        return executeOperation("getReferralWorkflow", () -> {
            try {
                Optional<Referral> referralOpt = repositoryManager.getReferralRepository().findById(referralId);
                if (!referralOpt.isPresent()) {
                    throw new HealthcareException("转诊不存在: " + referralId);
                }

                Referral referral = referralOpt.get();

                // 获取相关预约
                List<?> relatedAppointments = null;
                if (referral.getAppointmentId() != null) {
                    relatedAppointments = java.util.Arrays.asList(
                        repositoryManager.getAppointmentRepository().findById(referral.getAppointmentId()).orElse(null)
                    );
                }

                return new ReferralWorkflow(referral, relatedAppointments);

            } catch (HealthcareException e) {
                logServiceEvent("获取转诊工作流失败: " + e.getMessage());
                throw e;
            }
        });
    }

    /**
     * 验证转诊相关实体存在
     */
    private void validateReferralEntities(String patientId, String referringClinicianId, String referredToClinicianId,
                                        String referringFacilityId, String referredToFacilityId) throws HealthcareException {
        validateBusinessRule(repositoryManager.getPatientRepository().existsById(patientId),
                           "患者不存在: " + patientId);
        validateBusinessRule(repositoryManager.getClinicianRepository().existsById(referringClinicianId),
                           "转诊医生不存在: " + referringClinicianId);
        validateBusinessRule(repositoryManager.getClinicianRepository().existsById(referredToClinicianId),
                           "接收医生不存在: " + referredToClinicianId);
        validateBusinessRule(repositoryManager.getFacilityRepository().existsById(referringFacilityId),
                           "转诊设施不存在: " + referringFacilityId);
        validateBusinessRule(repositoryManager.getFacilityRepository().existsById(referredToFacilityId),
                           "接收设施不存在: " + referredToFacilityId);
    }

    /**
     * 验证转诊数据
     */
    private void validateReferralData(Referral referral) throws HealthcareException {
        validateBusinessRule(referral.isValid(), "转诊数据不符合要求");

        // 验证转诊人和接收人不是同一个人
        validateBusinessRule(!referral.getReferringClinicianId().equals(referral.getReferredToClinicianId()),
                           "转诊医生和接收医生不能是同一个人");

        // 验证转诊设施和接收设施不是同一个
        validateBusinessRule(!referral.getReferringFacilityId().equals(referral.getReferredToFacilityId()),
                           "转诊设施和接收设施不能是同一个");
    }

    /**
     * 检查优先级是否更高
     */
    private boolean isHigherUrgency(String newLevel, String currentLevel) {
        // 定义优先级顺序：Urgent > Routine > Non-urgent
        java.util.Map<String, Integer> urgencyOrder = new java.util.HashMap<>();
        urgencyOrder.put("Non-urgent", 1);
        urgencyOrder.put("Routine", 2);
        urgencyOrder.put("Urgent", 3);

        Integer newPriority = urgencyOrder.get(newLevel);
        Integer currentPriority = urgencyOrder.get(currentLevel);

        return newPriority != null && currentPriority != null && newPriority > currentPriority;
    }

    /**
     * 生成转诊ID
     */
    private String generateReferralId() {
        return "R" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 获取转诊过期提醒
     */
    public List<Referral> getOverdueReferrals() throws HealthcareException {
        validateInitialized();

        return executeOperation("getOverdueReferrals", () -> {
            try {
                return repositoryManager.getReferralRepository().findOverdueReferrals();

            } catch (Exception e) {
                logServiceEvent("获取过期转诊失败: " + e.getMessage());
                throw new HealthcareException("获取过期转诊失败", e);
            }
        });
    }

    @Override
    public String getServiceStatus() {
        return getBasicStatus() +
               ", 转诊总数: " + (initialized ?
                   repositoryManager.getReferralRepository().getCacheSize() : "未知");
    }

    @Override
    public String getStatistics() {
        if (!initialized) {
            return "服务未初始化";
        }

        try {
            StringBuilder stats = new StringBuilder();
            stats.append("转诊服务统计:\n");
            stats.append("- 转诊总数: ").append(repositoryManager.getReferralRepository().count()).append("\n");
            stats.append(repositoryManager.getReferralRepository().getReferralStatistics());
            return stats.toString();
        } catch (Exception e) {
            return "获取统计信息失败: " + e.getMessage();
        }
    }

    /**
     * 转诊工作流内部类
     */
    public static class ReferralWorkflow {
        private final Referral referral;
        private final List<?> relatedAppointments;

        public ReferralWorkflow(Referral referral, List<?> relatedAppointments) {
            this.referral = referral;
            this.relatedAppointments = relatedAppointments != null ? relatedAppointments :
                                    new java.util.ArrayList<>();
        }

        public Referral getReferral() { return referral; }
        public List<?> getRelatedAppointments() { return relatedAppointments; }

        public String getWorkflowStatus() {
            StringBuilder status = new StringBuilder();
            status.append("转诊状态: ").append(referral.getStatus()).append("\n");
            status.append("优先级: ").append(referral.getUrgencyLevel()).append("\n");
            status.append("天数: ").append(referral.getDaysSinceReferral()).append("\n");
            status.append("相关预约: ").append(relatedAppointments.size()).append("个\n");
            return status.toString();
        }
    }
}
