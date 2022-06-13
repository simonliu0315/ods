package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsDanResourceStus;
import gov.sls.ods.dto.Ods711xViewDto;
import gov.sls.ods.dto.Ods711xWorkbookDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cht.commons.persistence.query.Query;
import com.cht.commons.persistence.query.Query.Builder;
import com.cht.commons.persistence.query.SqlExecutor;

public class OdsDanResourceStusRepositoryImpl implements
        OdsDanResourceStusRepositoryCustom {

    @Autowired
    private SqlExecutor executor;

    @Override
    public List<Ods711xWorkbookDto> findNotImportOdsWorkbook() {
        Builder query = Query.builder();

        query.append(" select ");
        query.append("     a.id, a.RESOURCE_ID, a.RESOURCE_TYPE, ");
        query.append("     a.REFRESH_TIME, a.EXPORT_STUS, ");
        query.append("     a.IMPORT_ODS_STUS, ");
        query.append("     (select max(ver) from ods_workbook ia where ia.id = a.RESOURCE_ID ) maxVer, ");
        query.append("     name");
        query.append(" from ");
        query.append("     ods_dan_resource_stus a ");
        query.append("     inner join ods_dan_workbook b  ");
        query.append("        on a.resource_type = '1' and a.resource_id = b.id ");
        query.append(" where ");
        query.append("     a.export_stus = 'Y' ");
        query.append("     AND a.import_ods_stus is null ");

        return executor.queryForList(query.build(), Ods711xWorkbookDto.class);
    }
    
    @Override
    public List<Ods711xViewDto> findNotImportOdsView() {
        Builder query = Query.builder();

        query.append(" select ");
        query.append("       a.id, a.RESOURCE_ID, a.RESOURCE_TYPE, ");
        query.append("       a.REFRESH_TIME, a.EXPORT_STUS, ");
        query.append("       a.IMPORT_ODS_STUS, ");
        query.append("       (select max(ver) from ods_resource_version ia where ia.resource_id = b.id ) maxVer, ");
        query.append("       b.name, b.id odsResourceId, b.format, ");
        query.append("       c.WORKBOOK_ID workbookId ");
        query.append(" from ");
        query.append("       ods_dan_resource_stus a ");
        query.append("       inner join ods_resource b ");
        query.append("         on a.resource_type = '2' and a.resource_id = b.view_id  ");
        query.append("       inner join ods_dan_view c ");
        query.append("         on a.resource_type = '2' and a.resource_id = c.id ");
        query.append(" where ");
        query.append("       a.export_stus = 'Y' ");
        query.append("       AND a.import_ods_stus is null ");


        return executor.queryForList(query.build(), Ods711xViewDto.class);
    }
}
