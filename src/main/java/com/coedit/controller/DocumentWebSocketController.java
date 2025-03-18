package com.coedit.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;

@Controller
public class DocumentWebSocketController {

    //处理文档编辑操作
    @MessageMapping("/yjs/document/{docId}")   
    @SendTo("/topic/yjs/document/{docId}")//广播到所有订阅者
    public byte[] handleDocumentUpdate(
        @DestinationVariable String docId,
        @Payload byte[] yjsUpdate
    ) {
        // 1. 将更新保存到数据库（可选）
        // 2. 直接广播给其他客户端
        return yjsUpdate;
    }
}
