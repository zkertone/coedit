package com.coedit.model.entity;

import lombok.Data;

@Data
public class EditOperation {
    private String type; // 操作类型（insert/delete/format)
    private String position; // 操作位置
    private String text; // 操作文本
    private String userId; // 操作者ID（前段不需要传，后端自动填充）
}
