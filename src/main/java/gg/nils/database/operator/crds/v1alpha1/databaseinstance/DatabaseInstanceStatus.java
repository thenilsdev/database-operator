package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseInstanceStatus {

    private boolean status;

    private String message;
}
