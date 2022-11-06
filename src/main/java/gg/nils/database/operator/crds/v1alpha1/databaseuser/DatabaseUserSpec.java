package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import lombok.Data;

@Data
public class DatabaseUserSpec {

    private Connection connection;

    private String name;

    @Data
    public static class Connection {

        private String type;

        private String uri;

    }
}
