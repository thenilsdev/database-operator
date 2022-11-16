package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseInstanceStatus {

    private boolean status;

    private String message;
}
