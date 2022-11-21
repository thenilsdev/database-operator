package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import io.fabric8.kubernetes.api.model.SecretKeySelector;
import lombok.Data;

@Data
public class DatabaseInstanceSpec {

    private String type;

    private String uri;

    private SecretKeySelector uriRef;

    private String host;

    private SecretKeySelector hostRef;

    private String database;

    private SecretKeySelector databaseRef;

    private String username;

    private SecretKeySelector usernameRef;

    private String password;

    private SecretKeySelector passwordRef;
}
