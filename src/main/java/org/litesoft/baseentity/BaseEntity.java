package org.litesoft.baseentity;

import java.util.UUID;

public interface BaseEntity {
    UUID getId();

    Long getVersion();
}
