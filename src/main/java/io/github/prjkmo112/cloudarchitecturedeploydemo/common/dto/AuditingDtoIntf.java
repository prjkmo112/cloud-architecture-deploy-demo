package io.github.prjkmo112.cloudarchitecturedeploydemo.common.dto;

import java.time.LocalDateTime;

public interface AuditingDtoIntf {

    LocalDateTime createdAt();

    LocalDateTime modifiedAt();

}
