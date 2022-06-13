package gov.sls.ods.repository;

import gov.sls.ods.dto.PackageMetadataDto;
import gov.sls.ods.dto.Ods703eTab2Dto;

import java.util.List;

public interface OdsPackageMetadataRepositoryCustom {

    public List<PackageMetadataDto> getUnionPackageExtra(String packageId, int packageVer);

    public List<Ods703eTab2Dto> getPackageMetatemplate(String packageId);

    public void deleteByPackageId(String packageId);

    /**
     * join ODS_PACKAGE_METADATA, ODS_PACKAGE_EXTRA, ODS_PACKAGE_VERSION_METADATA,
     * ODS_PACKAGE_VERSION_EXTRA 取得metadata
     * 
     * @param packageId
     *            packageId
     * @param ver
     *            ver
     * @return List<PackageMetadataDto>
     */
    public List<PackageMetadataDto> findMetadataByPackageIdAndVer(String packageId, int ver);
}
