package org.litesoft.baseentity;

import java.util.UUID;

public interface BaseEntity {
    Long getVersion();

    UUID getId();
}
