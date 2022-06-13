package gov.sls.ods.repository;

import gov.sls.ods.dto.Ods303eIndividualDto;
import gov.sls.ods.dto.Ods773xIndividualDto;

import java.util.List;

public interface OdsIndividePackageSubRepositoryCustom {

    public List<Ods303eIndividualDto> getIndPackageSub(String packageCode, String userId);
    
    public void deleteByUserUnifyIdPackageIdList(String userUnifyId,
            List<String> packageIdListAry);
    
    public List<Ods773xIndividualDto> findNotifier();

    /**使用resourceId查詢 有訂閱該包含該resourceId之主題的UserUnifyId
     * @param resourceId resourceId
     * @return List<String> UserUnifyId list
     */
    public List<String> findUserUnifyIdByResourceId(String resourceId);

}
