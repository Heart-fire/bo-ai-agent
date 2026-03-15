package com.xinhuo.boaiagent.agent;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xinhuo.boaiagent.agent.model.StructuredMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return 行动执行结果
     */
    public abstract String act();

    /**
     * 执行单个步骤：思考和行动
     *
     * @return 步骤执行结果（JSON 格式）
     */
    @Override
    public String step() {
        try {
            // 先思考
            boolean shouldAct = think();

            // 生成思考 JSON
            StructuredMessage stepMessage = StructuredMessage.builder()
                    .type(StructuredMessage.MessageType.STEP)
                    .step(StructuredMessage.StepInfo.builder()
                            .number(getCurrentStep())
                            .thought(getThoughtContent())
                            .build())
                    .build();

            String stepJson = JSONUtil.toJsonStr(stepMessage);

            if (!shouldAct) {
                // 只有思考，没有行动
                return stepJson;
            }

            // 再行动
            String actionResult = act();

            // 组合：思考 JSON + 行动 JSON
            // 返回一个 JSON 数组，包含两个消息
            return "[" + stepJson + ", " + actionResult + "]";

        } catch (Exception e) {
            // 记录异常日志
            log.error("步骤执行失败", e);
            StructuredMessage errorMessage = StructuredMessage.error("步骤执行失败：" + e.getMessage());
            return JSONUtil.toJsonStr(errorMessage);
        }
    }

    /**
     * 获取思考内容（子类可以重写）
     *
     * @return 思考内容
     */
    protected String getThoughtContent() {
        return "";
    }

}