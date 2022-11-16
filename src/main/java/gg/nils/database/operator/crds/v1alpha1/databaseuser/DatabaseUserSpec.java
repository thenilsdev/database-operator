package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import lombok.Data;

@Data
public class DatabaseUserSpec {

    private String databaseInstance;

    private String name;
}
