package com.xinhuo.boaiagent.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 结构化消息模型，用于前端渲染
 */
@Data
@Builder
public class StructuredMessage {

    /**
     * 消息类型
     */
    @JsonProperty("type")
    private MessageType type;

    /**
     * 步骤信息（仅 step 类型）
     */
    @JsonProperty("step")
    private StepInfo step;

    /**
     * 总结内容（仅 summary 类型）
     */
    @JsonProperty("summary")
    private SummaryInfo summary;

    /**
     * 错误信息（仅 error 类型）
     */
    @JsonProperty("error")
    private String error;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /** 思考步骤 */
        STEP,
        /** 工具执行结果 */
        TOOL_RESULT,
        /** 任务总结 */
        SUMMARY,
        /** 错误 */
        ERROR
    }

    /**
     * 步骤信息
     */
    @Data
    @Builder
    public static class StepInfo {
        /** 步骤号 */
        @JsonProperty("number")
        private int number;

        /** 思考内容 */
        @JsonProperty("thought")
        private String thought;

        /** 准备执行的工具 */
        @JsonProperty("plannedTool")
        private PlannedTool plannedTool;
    }

    /**
     * 计划执行的工具
     */
    @Data
    @Builder
    public static class PlannedTool {
        /** 工具名称 */
        @JsonProperty("name")
        private String name;

        /** 工具描述 */
        @JsonProperty("description")
        private String description;

        /** 参数预览 */
        @JsonProperty("argsPreview")
        private String argsPreview;
    }

    /**
     * 工具执行结果
     */
    @Data
    @Builder
    public static class ToolResultInfo {
        /** 步骤号 */
        @JsonProperty("stepNumber")
        private int stepNumber;

        /** 工具名称 */
        @JsonProperty("toolName")
        private String toolName;

        /** 工具状态 */
        @JsonProperty("status")
        private String status; // success, error

        /** 结果内容 */
        @JsonProperty("content")
        private ResultContent content;
    }

    /**
     * 结果内容（支持多种类型）
     */
    @Data
    @Builder
    public static class ResultContent {
        /** 文本内容 */
        @JsonProperty("text")
        private String text;

        /** 图片列表 */
        @JsonProperty("images")
        private List<ImageItem> images;

        /** 链接列表 */
        @JsonProperty("links")
        private List<LinkItem> links;

        /** 文件列表 */
        @JsonProperty("files")
        private List<FileItem> files;
    }

    /**
     * 图片项
     */
    @Data
    @Builder
    public static class ImageItem {
        /** 图片 URL */
        @JsonProperty("url")
        private String url;

        /** 图片描述 */
        @JsonProperty("description")
        private String description;

        /** 缩略图 URL */
        @JsonProperty("thumbnail")
        private String thumbnail;
    }

    /**
     * 链接项
     */
    @Data
    @Builder
    public static class LinkItem {
        /** 链接 URL */
        @JsonProperty("url")
        private String url;

        /** 链接标题 */
        @JsonProperty("title")
        private String title;

        /** 链接描述 */
        @JsonProperty("description")
        private String description;
    }

    /**
     * 文件项
     */
    @Data
    @Builder
    public static class FileItem {
        /** 文件名 */
        @JsonProperty("name")
        private String name;

        /** 文件路径 */
        @JsonProperty("path")
        private String path;

        /** 文件大小 */
        @JsonProperty("size")
        private Long size;
    }

    /**
     * 总结信息
     */
    @Data
    @Builder
    public static class SummaryInfo {
        /** 总步骤数 */
        @JsonProperty("totalSteps")
        private int totalSteps;

        /** 核心结论 */
        @JsonProperty("conclusion")
        private String conclusion;

        /** 关键信息列表 */
        @JsonProperty("keyPoints")
        private List<String> keyPoints;

        /** 相关资源 */
        @JsonProperty("resources")
        private List<ResourceItem> resources;

        /** 执行的工具列表 */
        @JsonProperty("toolsUsed")
        private List<String> toolsUsed;
    }

    /**
     * 资源项
     */
    @Data
    @Builder
    public static class ResourceItem {
        /** 资源类型 */
        @JsonProperty("type")
        private String type; // image, link, file

        /** 资源内容 */
        @JsonProperty("data")
        private Object data;
    }

    // ========== 便捷创建方法 ==========

    public static StructuredMessage step(int number, String thought) {
        return StructuredMessage.builder()
                .type(MessageType.STEP)
                .step(StepInfo.builder()
                        .number(number)
                        .thought(thought)
                        .build())
                .build();
    }

    public static StructuredMessage toolResult(int stepNumber, String toolName, String content) {
        return StructuredMessage.builder()
                .type(MessageType.TOOL_RESULT)
                .step(StepInfo.builder()
                        .number(stepNumber)
                        .build())
                .build();
    }

    public static StructuredMessage error(String error) {
        return StructuredMessage.builder()
                .type(MessageType.ERROR)
                .error(error)
                .build();
    }

    public static StructuredMessage summary(String conclusion, List<String> keyPoints) {
        return StructuredMessage.builder()
                .type(MessageType.SUMMARY)
                .summary(SummaryInfo.builder()
                        .conclusion(conclusion)
                        .keyPoints(keyPoints)
                        .build())
                .build();
    }
}
