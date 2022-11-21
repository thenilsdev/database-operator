package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseUserStatus {

    private boolean status;

    private String message;
}
