package kr.co.nice.nicein.auth.entity;

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
@Table(name = "auth_menu")
public class AuthMenu {

    @Id
    private String role;

    private String showHr;
    private String showHrManage;
    private String showHrTotal;
    private String showHrIt;
    private String showHrRetire;

    private String showRpa;

    private String showMan;
    private String showManPw;
    private String showManAccount;
    private String showManRoleHistory;
    private String showManLoginHistory;
    private String showManExcelHistory;
    private String showManHrHistory;
    private String showManOption;

}
