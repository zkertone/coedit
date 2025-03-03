package com.coedit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
   @Id
   private String id;

   @Indexed(unique = true)
   private String username;

   private String password;//加密存储

   @Indexed(unique = true)
   private String email;

   private List<String> roles = Collections.singletonList("USER");

   private Boolean enabled = true;
}
