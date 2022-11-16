package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseUserStatus {

    private boolean status;

    private String message;
}
