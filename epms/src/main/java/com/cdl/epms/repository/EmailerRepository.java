package com.cdl.epms.repository;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.common.enums.EmailerStatus;
import com.cdl.epms.model.Emailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailerRepository extends JpaRepository<Emailer, Long> {

    Optional<Emailer> findByCycleTypeAndTemplateTypeAndStatus(
            CycleType cycleType,
            EmailTemplateType templateType,
            EmailerStatus status
    );

}