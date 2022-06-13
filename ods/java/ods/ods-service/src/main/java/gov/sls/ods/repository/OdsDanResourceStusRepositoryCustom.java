package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods711xViewDto;
import gov.sls.ods.dto.Ods711xWorkbookDto;

import java.util.List;


public interface OdsDanResourceStusRepositoryCustom {

    public List<Ods711xWorkbookDto> findNotImportOdsWorkbook();
    
    
    public List<Ods711xViewDto> findNotImportOdsView();

}
