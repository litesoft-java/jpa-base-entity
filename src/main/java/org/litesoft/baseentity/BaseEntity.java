package org.litesoft.baseentity;

import java.util.UUID;

public interface BaseEntity<T extends BaseEntity<T>> {
    Class<T> type();

    Long getVersion();

    UUID getId();
}
