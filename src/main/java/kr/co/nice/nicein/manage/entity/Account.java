package kr.co.nice.nicein.manage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vaccount")
public class Account {
    private String targetCompanyId;
    private String companyId;
    private String companyName;
    private String name;

    @Id
    @Column(name="username")
    private String loginId;

    private String authDetails;

    private String authOption;
    private String authRole;
    private String authRw;
    private String note;
}
