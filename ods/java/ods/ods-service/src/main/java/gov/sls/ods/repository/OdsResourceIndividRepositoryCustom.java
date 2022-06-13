package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceIndivid;
import gov.sls.ods.dto.Ods303eIndividualDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdsResourceIndividRepositoryCustom {

    public Page<Ods303eIndividualDto> getResourceDate(String userUnifyId, Pageable pageable);
    
    public void updateNotifyMk(String userUnifyId, String mk);
    
    public List<OdsResourceIndivid> queryDanExportStatus(String userUnifyId, String resourceId);

}
