package com.coedit.controller;

import com.coedit.model.entity.EditOperation;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DocumentWebSocketController {

    //处理文档编辑操作
    @MessageMapping("/document/{docId}/edit")   
    @SendTo("/topic/document/{docId}")//广播到所有订阅者
    public EditOperation handleEdit(EditOperation operation) {
        return operation;
    }
}
