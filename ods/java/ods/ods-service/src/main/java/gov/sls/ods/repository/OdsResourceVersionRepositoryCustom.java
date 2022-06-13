package gov.sls.ods.repository;

import gov.sls.entity.ods.OdsResourceVersion;

import java.util.List;

public interface OdsResourceVersionRepositoryCustom {

    /**
     * 取得素材及案例
     * 
     * @param packageId
     *            packageId
     * @param ver
     *            ver
     * @return List<OdsResourceVersion>
     */
    public List<OdsResourceVersion> findResourceByPackageIdAndVer(String packageId, int ver);

}
