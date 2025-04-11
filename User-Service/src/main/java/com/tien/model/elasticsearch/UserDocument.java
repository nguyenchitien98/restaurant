package com.tien.model.elasticsearch;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

//indexName: Chỉ định tên index trong Elasticsearch tương ứng với class này
//@Document: Nó dùng để đánh dấu class đó là một Elasticsearch document (bản ghi trong index), tương tự như @Entity trong JPA.
@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    @Id
    private Long userId;

    private String username;
    private String email;
    private String role;
}
