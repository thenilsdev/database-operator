package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import lombok.Data;

@Data
public class DatabaseInstanceSpec {

    private String type;

    private String uri;
}
