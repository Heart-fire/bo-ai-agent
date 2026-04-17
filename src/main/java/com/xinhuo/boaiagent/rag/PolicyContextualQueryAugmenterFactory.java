package com.xinhuo.boaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 政策问答上下文查询增强器工厂
 * 当向量库中检索不到相关文档时，返回友好的兜底提示，引导用户通过官方渠道咨询
 */
public class PolicyContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，当前知识库中暂未收录与您问题相关的政策文档。
                建议您通过以下官方渠道获取准确信息：
                - 北京市政务服务热线：12345
                - 北京市人民政府网：https://banshi.beijing.gov.cn
                请以官方力资源和社会保障局官网：rsj.beijing.gov.cn
                - 北京市政务服务网最新公告为准，如有变化请及时关注官网更新。
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
