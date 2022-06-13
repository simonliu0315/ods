package gov.sls.ods.service;

import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsCategory;
import gov.sls.entity.ods.OdsPackage;
import gov.sls.entity.ods.OdsPackageDocument;
import gov.sls.entity.ods.OdsPackageExtra;
import gov.sls.entity.ods.OdsPackageLayout;
import gov.sls.entity.ods.OdsPackageLayoutPK;
import gov.sls.entity.ods.OdsPackageMetadata;
import gov.sls.entity.ods.OdsPackageResource;
import gov.sls.entity.ods.OdsPackageResourcePK;
import gov.sls.entity.ods.OdsPackageTag;
import gov.sls.entity.ods.OdsPackageVersion;
import gov.sls.entity.ods.OdsPackageVersionExtra;
import gov.sls.entity.ods.OdsPackageVersionMetadata;
import gov.sls.entity.ods.OdsPackageVersionPK;
import gov.sls.entity.ods.OdsResourceVersion;
import gov.sls.ods.dto.Ods703eGridDto;
import gov.sls.ods.dto.Ods703eTab2DialogDto;
import gov.sls.ods.dto.Ods703eTab2Dto;
import gov.sls.ods.repository.OdsCategoryRepository;
import gov.sls.ods.repository.OdsGroupPackageRepository;
import gov.sls.ods.repository.OdsNoticePackageVersionRepository;
import gov.sls.ods.repository.OdsPackageDocumentRepository;
import gov.sls.ods.repository.OdsPackageExtraRepository;
import gov.sls.ods.repository.OdsPackageLayoutRepository;
import gov.sls.ods.repository.OdsPackageMetadataRepository;
import gov.sls.ods.repository.OdsPackageRepository;
import gov.sls.ods.repository.OdsPackageResourceRepository;
import gov.sls.ods.repository.OdsPackageTagRepository;
import gov.sls.ods.repository.OdsPackageVersionExtraRepository;
import gov.sls.ods.repository.OdsPackageVersionMetadataRepository;
import gov.sls.ods.repository.OdsPackageVersionRepository;
import gov.sls.ods.repository.OdsResourceRepository;
import gov.sls.ods.repository.OdsResourceVersionRepository;
import gov.sls.ods.repository.OdsUserFollowPackageRepository;
import gov.sls.ods.repository.OdsUserPackageRateRepository;
import gov.sls.ods.repository.OdsUserPackageVersionClickRepository;
import gov.sls.ods.repository.OdsUserPackageVersionDownloadRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cht.sac.uaa.m400.service.bean.UAA400Bean;
import com.cht.sac.uaa.m400.service.bean.UAA492Bean;
import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods703eService {

    @Autowired
    private OdsPackageRepository odsPackageRepository;
    
    @Autowired
    private OdsPackageTagRepository odsPackageTagRepository;
    
    @Autowired
    private OdsPackageMetadataRepository odsPackageMetadataRepository;
    
    @Autowired
    private OdsPackageExtraRepository odsPackageExtraRepository;
    
    @Autowired
    private OdsPackageVersionRepository odsPackageVersionRepository;
    
    @Autowired
    private OdsPackageDocumentRepository odsPackageDocumentRepository;
    
   @Autowired
    private OdsResourceRepository odsResourceRepository;
    
    @Autowired
    private OdsResourceVersionRepository odsResourceVersionRepository;
    
    @Autowired
    private OdsCategoryRepository odsCategoryRepository;
    
    @Autowired
    private OdsPackageResourceRepository odsPackageResourceRepository;
    
    @Autowired
    private OdsPackageLayoutRepository odsPackageLayoutRepository;
    
    @Autowired
    private OdsNoticePackageVersionRepository odsNoticePackageVersionRepository;
    
    @Autowired
    private OdsUserFollowPackageRepository odsUserFollowPackageRepository;
    
    @Autowired
    private OdsUserPackageRateRepository odsUserPackageRateRepository;
    
    @Autowired
    private OdsUserPackageVersionClickRepository odsUserPackageVersionClickRepository;
    
    @Autowired
    private Ods303eService ods303eService;
    
    @Autowired
    private OdsPackageVersionMetadataRepository odsPackageVersionMetadataRepository;
    
    @Autowired
    private OdsPackageVersionExtraRepository odsPackageVersionExtraRepository;
    
    @Autowired
    private OdsGroupPackageRepository odsGroupPackageRepository;
    
    @Autowired
    private OdsUserPackageVersionDownloadRepository odsUserPackageVersionDownloadRepository;
    
    @Autowired
    private UaaAuthoriy uaaAuthoriy;
    
    @Autowired
    private PackResService packResService;
    

    public List<OdsPackage> findByName(String name, String description) {        
        if (!Strings.isNullOrEmpty(name) || !Strings.isNullOrEmpty(description)) {
            return odsPackageRepository.findByNameLikeAndDescriptionLike("%" + name + "%", "%" + description + "%");
        }        
        return odsPackageRepository.findAll();
    }
    
    public List<Ods703eGridDto> findPackageAndVersionByName(String name, String description) {
        return odsPackageRepository.findPackageAndVersion(name, description);
    }
    
    public List<OdsPackageTag> findPackageTagsByPackageId(String packageId) {
        return odsPackageTagRepository.findByPackageId(packageId);
    }
    
    public List<Ods703eTab2Dto> findMetadataByPackageId(String packageId) {
        return odsPackageMetadataRepository.getPackageMetatemplate(Strings.emptyToNull(packageId));
    }
    
    public List<OdsPackageExtra> findExtraMetadataByPackageId(String packageId) {
            return odsPackageExtraRepository.findByPackageIdOrderByPositionsAsc(Strings.emptyToNull(packageId));
    }

    public String create(OdsPackage odsPackage, List<OdsPackageTag> odsPackageTagList, 
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageExtra> odsPackageExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        //檢查名稱是否有重覆, 如果沒有才新增
        if (odsPackageRepository.findByName(odsPackage.getName()).isEmpty()) {
            OdsPackage op = new OdsPackage();
            op.setName(odsPackage.getName());
            op.setDescription(odsPackage.getDescription());
            //op.setImageUrl(odsPackage.getImageUrl());
            op.setCreated(date);
            op.setUpdated(date);
            op.setCreateUserId(userId);
            op.setUpdateUserId(userId);
            op.setType(odsPackage.getType());
            op.setCode(odsPackage.getCode());
            odsPackageRepository.create(op);            
            //處理PackageTags, PackageMetadata, ExtraMetadata
            processTagsAndMetadata(op, odsPackageTagList, packageMetatemplateDto, odsPackageExtraList);
            
            return "";
        } else {
            return "duplicated";
        }
    }
    
    public void save(OdsPackage odsPackage, List<OdsPackageTag> odsPackageTagList, 
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageExtra> odsPackageExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        odsPackage.setUpdated(date);
        odsPackage.setUpdateUserId(userId);
        odsPackageRepository.save(odsPackage);
        //處理PackageTags, PackageMetadata, ExtraMetadata
        processTagsAndMetadata(odsPackage, odsPackageTagList, packageMetatemplateDto, odsPackageExtraList);
        
        //產生版本預覽圖
        List<OdsPackageVersion> pvList = odsPackageVersionRepository.getPackageVersionAll(odsPackage.getId());
        if (!pvList.isEmpty()) {
            for (OdsPackageVersion pv : pvList) {
                if (!Strings.isNullOrEmpty(pv.getPattern()) && !"02".equals(odsPackage.getType())) {
                    ods303eService.generateTemplate(pv.getId().getPackageId(), pv.getId().getVer());
                    //打包Resources
                    try {
                        packResService.createCompressedFile(pv.getId().getPackageId(), String.valueOf(pv.getId().getVer()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        
        //UAA資料權查詢
        UAA400Bean uaa400Bean = uaaAuthoriy.queryDataRuleByTypeIdRuleId(odsPackage.getId());
        
        //有查到資料再更新
        uaaAuthoriy.updateDataRuleByTypeIdRuleId(uaa400Bean, odsPackage.getName());
        
    }
    
    public List<UAA492Bean> uaaCheck(OdsPackage odsPackage) {
        // 查出來的結果為資料權群組
        return uaaAuthoriy.uaaCheck(odsPackage);
    }

    public void delete(OdsPackage odsPackage) {        
        //刪除OdsPackageTag
        odsPackageTagRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsPackageMeta
        odsPackageMetadataRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsPackageExtra
        odsPackageExtraRepository.deleteByPackageId(odsPackage.getId());        
        //刪除OdsPackageVersionMetadata
        odsPackageVersionMetadataRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsPackageVersionExtra
        odsPackageVersionExtraRepository.deleteByPackageId(odsPackage.getId());        
        //刪除OdsPackageLayout
        odsPackageLayoutRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsPackageResource
        odsPackageResourceRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsNoticePackageVersion
        odsNoticePackageVersionRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsUserFollowPackage
        odsUserFollowPackageRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsUserPackageRate
        odsUserPackageRateRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsUserPackageVersionClick
        odsUserPackageVersionClickRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsUserPackageVersionDownload
        odsUserPackageVersionDownloadRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsPackageVersion
        odsPackageVersionRepository.deleteByPackageId(odsPackage.getId());
        //刪除OdsGroupPackage
        odsGroupPackageRepository.deleteByPackageId(odsPackage.getId());
        //最後刪除OdsPackage
        odsPackageRepository.delete(odsPackage.getId());
        
        // 利用資料規則類型、資料權代碼刪除資料權
        
        int deleteSize = uaaAuthoriy.deleteDataRuleByTypeIdRuleId(odsPackage.getId());
        log.debug("deleteSize" + deleteSize);
    }
    
    private void processTagsAndMetadata(OdsPackage odsPackage, List<OdsPackageTag> odsPackageTagList, 
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageExtra> odsPackageExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        //新增OdsPackageTag, 先刪除再新增
        odsPackageTagRepository.deleteByPackageId(odsPackage.getId());
        if (!odsPackageTagList.isEmpty()) {
            for (OdsPackageTag opt : odsPackageTagList) {
                OdsPackageTag newOpt = new OdsPackageTag();
                newOpt.setPackageId(odsPackage.getId());
                newOpt.setTagName(opt.getTagName());
                newOpt.setCreated(date);
                newOpt.setUpdated(date);
                newOpt.setCreateUserId(userId);
                newOpt.setUpdateUserId(userId);
                odsPackageTagRepository.create(newOpt);
            }
        }
        
        //新增OdsPackageMetadata, 先刪除再新增
        odsPackageMetadataRepository.deleteByPackageId(odsPackage.getId());
        if (!packageMetatemplateDto.isEmpty()) {
            for (Ods703eTab2Dto opm : packageMetatemplateDto) {
                OdsPackageMetadata newOpm = new OdsPackageMetadata();
                newOpm.setPackageId(odsPackage.getId());
                newOpm.setMetadataId(opm.getMetadataId());
                newOpm.setPositions(Integer.parseInt(opm.getPositions()));
                newOpm.setDataType(opm.getDataType());
                newOpm.setDataKey(opm.getDataKey());
                newOpm.setDataValue(opm.getDataValue());
                newOpm.setCreated(date);
                newOpm.setUpdated(date);
                newOpm.setCreateUserId(userId);
                newOpm.setUpdateUserId(userId);
                odsPackageMetadataRepository.create(newOpm);
            }
        }
        
        //新增OdsPackageExtra, 先刪除再新增
        odsPackageExtraRepository.deleteByPackageId(odsPackage.getId());
        if (!odsPackageExtraList.isEmpty()) {
            int positions = 0;
            for (OdsPackageExtra ope : odsPackageExtraList) {
                OdsPackageExtra newOpe = new OdsPackageExtra();
                newOpe.setPackageId(odsPackage.getId());
                newOpe.setPositions(positions++);
                newOpe.setDataType("text");
                newOpe.setDataKey(ope.getDataKey());
                newOpe.setDataValue(ope.getDataValue());
                newOpe.setCreated(date);
                newOpe.setUpdated(date);
                newOpe.setCreateUserId(userId);
                newOpe.setUpdateUserId(userId);
                odsPackageExtraRepository.create(newOpe);
            }
        }
    }
    
    public void updateImageUrl(String packageId) {        
        odsPackageRepository.updateImageUrl(packageId);
    }
    
    public OdsPackageDocument createPackageDocument(String packageId,String filename, String description) {
        String userId = UserHolder.getUser().getId();
        log.debug("userId="+userId);
        log.debug("filename="+filename);
        Date currentTime=Calendar.getInstance().getTime();
        OdsPackageDocument entity=new OdsPackageDocument();
        entity.setPackageId(packageId);
        entity.setDescription(description);
        entity.setFilename(filename);
        entity.setCreateUserId(userId);
        entity.setUpdateUserId(userId);
        entity.setCreated(currentTime);
        entity.setUpdated(currentTime);
        return odsPackageDocumentRepository.saveAndFlush(entity);
    }

    public List<OdsPackageDocument> findPackageDocumentByPackageId(String packageId) {
        return odsPackageDocumentRepository.findByPackageId(packageId);
    }
    
    public void deletePackageDocumentById(String id) {
        odsPackageDocumentRepository.delete(id);
    }
    
    public OdsPackageDocument getPackageDocumentById(String id) {
        return odsPackageDocumentRepository.findOne(id);
    }
    //===============PackageVer Zone==============================
    public List<OdsPackageVersion> findPackageVersionByPackageId(String packageId) {
        return odsPackageVersionRepository.getPackageVersionWithoutDelMk(Strings.emptyToNull(packageId));
    }
    
    public List<OdsPackageVersion> findPackageVersionAllByPackageId(String packageId) {
        return odsPackageVersionRepository.getPackageVersionAll(Strings.emptyToNull(packageId));
    }
    
    public List<Ods703eTab2Dto> findPackageVersionMetadata(String packageId, String packageVer) {
        return odsPackageVersionMetadataRepository.getPackageVersionMetatemplate(Strings.emptyToNull(packageId), Integer.parseInt(packageVer));
    }
    
    public List<OdsPackageVersionExtra> findPackageVersionExtra(String packageId, String packageVer) {
            return odsPackageVersionExtraRepository.findByPackageIdAndPackageVerOrderByPositionsAsc(Strings.emptyToNull(packageId), Integer.parseInt(packageVer));
    }
    
    public void createVer(OdsPackageVersion odsPackageVersion, List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList, 
            List<OdsPackageLayout> odsPackageLayoutList, 
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageVersionExtra> odsPackageVersionExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        Integer maxVerList = odsPackageVersionRepository.getMaxPackageVersion(odsPackageVersion.getId().getPackageId());
        int maxVer = odsPackageVersionRepository.getMaxPackageVersion(odsPackageVersion.getId().getPackageId()) != null ? maxVerList.intValue() : 0;
        //odsPackageVersion.getId().setVer(++maxVer);
        OdsPackageVersionPK odsPackageVersionPK = odsPackageVersion.getId();
        odsPackageVersionPK.setVer(++maxVer);
        odsPackageVersion.setId(odsPackageVersionPK);
        odsPackageVersion.setCreated(date);
        odsPackageVersion.setUpdated(date);
        odsPackageVersion.setCreateUserId(userId);
        odsPackageVersion.setUpdateUserId(userId);
        odsPackageVersion.setVersionDatetime(date);
        odsPackageVersionRepository.create(odsPackageVersion);
        
        //處理PackageVersionMetadata, PackageVersionExtra
        processVersionMetadata(odsPackageVersion, packageMetatemplateDto, odsPackageVersionExtraList);
        
        //處理PackageResource and PackageLayout
        processPackageResourceAndPackageLayout(odsPackageVersion, ods703eTab2DialogDtoList, odsPackageLayoutList);
        
        OdsPackage odsPackage = odsPackageRepository.findById(odsPackageVersion.getId().getPackageId()).get(0);
        log.info("PackageType:" + odsPackage.getType());
        //產生版本預覽圖
        if (!Strings.isNullOrEmpty(odsPackageVersion.getPattern()) && !"02".equals(odsPackage.getType())) {
            ods303eService.generateTemplate(odsPackageVersion.getId().getPackageId(), odsPackageVersion.getId().getVer());
            
            //打包Resources
            try {
                packResService.createCompressedFile(odsPackageVersion.getId().getPackageId(), String.valueOf(odsPackageVersion.getId().getVer()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
        }
        
    }
    
    public void saveVer(OdsPackageVersion odsPackageVersion, List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList, 
            List<OdsPackageLayout> odsPackageLayoutList, 
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageVersionExtra> odsPackageVersionExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        odsPackageVersion.setUpdated(date);
        odsPackageVersion.setUpdateUserId(userId);
        odsPackageVersion.setVersionDatetime(date);        
        odsPackageVersionRepository.save(odsPackageVersion);
        
        //處理PackageVersionMetadata, PackageVersionExtra
        processVersionMetadata(odsPackageVersion, packageMetatemplateDto, odsPackageVersionExtraList);
        
        //處理版本0
        //processVerZero(odsPackageVersion);
        
        //處理PackageResource and PackageLayout
        processPackageResourceAndPackageLayout(odsPackageVersion, ods703eTab2DialogDtoList, odsPackageLayoutList);
        
        OdsPackage odsPackage = odsPackageRepository.findById(odsPackageVersion.getId().getPackageId()).get(0);
        log.info("PackageType:" + odsPackage.getType());
        
        //產生版本預覽圖
        if (!Strings.isNullOrEmpty(odsPackageVersion.getPattern()) && !"02".equals(odsPackage.getType())) {
            ods303eService.generateTemplate(odsPackageVersion.getId().getPackageId(), odsPackageVersion.getId().getVer());
            
            //打包Resources
            try {
                packResService.createCompressedFile(odsPackageVersion.getId().getPackageId(), String.valueOf(odsPackageVersion.getId().getVer()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
        }

    }
    
    private void processVersionMetadata(OdsPackageVersion odsPackageVersion,  
            List<Ods703eTab2Dto> packageMetatemplateDto, List<OdsPackageVersionExtra> odsPackageVersionExtraList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        
        //新增OdsPackageVersionMetadata, 先刪除再新增
        odsPackageVersionMetadataRepository.deleteByPackageIdAndPackageVer(odsPackageVersion.getId().getPackageId(), 
                odsPackageVersion.getId().getVer());
        if (!packageMetatemplateDto.isEmpty()) {
            for (Ods703eTab2Dto opvm : packageMetatemplateDto) {
                OdsPackageVersionMetadata newOpvm = new OdsPackageVersionMetadata();
                newOpvm.setPackageId(odsPackageVersion.getId().getPackageId());
                newOpvm.setPackageVer(odsPackageVersion.getId().getVer());
                newOpvm.setMetadataId(opvm.getMetadataId());
                newOpvm.setPositions(Integer.parseInt(opvm.getPositions()));
                newOpvm.setDataType(opvm.getDataType());
                newOpvm.setDataKey(opvm.getDataKey());
                newOpvm.setDataValue(opvm.getDataValue());
                newOpvm.setCreated(date);
                newOpvm.setUpdated(date);
                newOpvm.setCreateUserId(userId);
                newOpvm.setUpdateUserId(userId);
                odsPackageVersionMetadataRepository.create(newOpvm);
            }
        }
        
        //新增OdsPackageVersionExtra, 先刪除再新增
        odsPackageVersionExtraRepository.deleteByPackageIdAndPackageVer(odsPackageVersion.getId().getPackageId(), 
                odsPackageVersion.getId().getVer());
        if (!odsPackageVersionExtraList.isEmpty()) {
            int positions = 0;
            for (OdsPackageVersionExtra opve : odsPackageVersionExtraList) {
                OdsPackageVersionExtra newOpve = new OdsPackageVersionExtra();
                newOpve.setPackageId(odsPackageVersion.getId().getPackageId());
                newOpve.setPackageVer(odsPackageVersion.getId().getVer());
                newOpve.setPositions(positions++);
                newOpve.setDataType("text");
                newOpve.setDataKey(opve.getDataKey());
                newOpve.setDataValue(opve.getDataValue());
                newOpve.setCreated(date);
                newOpve.setUpdated(date);
                newOpve.setCreateUserId(userId);
                newOpve.setUpdateUserId(userId);
                odsPackageVersionExtraRepository.create(newOpve);
            }
        }
    }
    
    private void processVerZero(OdsPackageVersion odsPackageVersion) {
        //處理版本0, 先砍掉再新增        
        odsPackageVersionRepository.deletePackageVersionZero(odsPackageVersion.getId().getPackageId());
        //找出主題已發佈的最大版本
        List<OdsPackageVersion> odsPackageVersionList = 
                odsPackageVersionRepository.getPublishPackageVerMax(odsPackageVersion.getId().getPackageId());
        if (!odsPackageVersionList.isEmpty()) {
            OdsPackageVersion ov = new OdsPackageVersion();
            OdsPackageVersionPK ovpk = new OdsPackageVersionPK();            
            ovpk.setPackageId(odsPackageVersionList.get(0).getId().getPackageId());
            ovpk.setVer(0);
            ov.setId(ovpk);
            ov.setCreated(odsPackageVersionList.get(0).getCreated());
            ov.setCreateUserId(odsPackageVersionList.get(0).getCreateUserId());
            ov.setDelMk(odsPackageVersionList.get(0).getDelMk());
            ov.setDescription(odsPackageVersionList.get(0).getDescription());
            ov.setIsPublished(odsPackageVersionList.get(0).getIsPublished());
            ov.setName(odsPackageVersionList.get(0).getName());
            ov.setPattern(odsPackageVersionList.get(0).getPattern());
            ov.setUpdated(odsPackageVersionList.get(0).getUpdated());
            ov.setUpdateUserId(odsPackageVersionList.get(0).getUpdateUserId());
            ov.setVersionDatetime(odsPackageVersionList.get(0).getVersionDatetime());
            odsPackageVersionRepository.create(ov);
            
            OdsPackage odsPackage = odsPackageRepository.findById(odsPackageVersion.getId().getPackageId()).get(0);
            log.info("PackageType:" + odsPackage.getType());

            //產生版本0預覽圖
            if (!Strings.isNullOrEmpty(odsPackageVersionList.get(0).getPattern()) && !"02".equals(odsPackage.getType())) {
                ods303eService.generateTemplate(odsPackageVersionList.get(0).getId().getPackageId(), 0);
                
                //打包Resources
                try {
                    packResService.createCompressedFile(odsPackageVersion.getId().getPackageId(), String.valueOf(0));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public List<Ods703eTab2DialogDto> findResByNameAndCategory(String name, String catId) {
        return odsResourceRepository.findResByNameAndCategory(name, catId);
    }
    
    public List<OdsCategory> findCategory() {
        return odsCategoryRepository.findAll();
    }
    
    public List<OdsResourceVersion> findResourceVer(String resourceId) {
        return odsResourceVersionRepository.findResourceVersionWithoutDelMk(resourceId);
    }
    
    public List<Ods703eTab2DialogDto> findPackResByIdAndVer(String packageId, String packageVer) {
        return odsPackageResourceRepository.findPackResByIdAndVer(packageId, Integer.parseInt(packageVer));
    }
    
    public List<OdsPackageLayout> findPackageLayout(String packageId, String packageVer) {
        return odsPackageLayoutRepository.
                findByIdPackageIdAndIdPackageVerOrderByIdRowPositionAscIdColumnPositionAsc(packageId, Integer.parseInt(packageVer));
    }
    
    private void processPackageResourceAndPackageLayout(OdsPackageVersion odsPackageVersion, 
            List<Ods703eTab2DialogDto> ods703eTab2DialogDtoList, List<OdsPackageLayout> odsPackageLayoutList) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        //新增OdsPackageResource, 先刪除再新增       
        odsPackageLayoutRepository.deletePackageLayout(odsPackageVersion.getId().getPackageId(), odsPackageVersion.getId().getVer());
        //新增OdsPackageResource, 先刪除再新增       
        odsPackageResourceRepository.deletePackageResource(odsPackageVersion.getId().getPackageId(), odsPackageVersion.getId().getVer());
        //新增OdsPackageResource
        if (!ods703eTab2DialogDtoList.isEmpty()) {
            for (Ods703eTab2DialogDto opd : ods703eTab2DialogDtoList) {
                OdsPackageResource opr = new OdsPackageResource();
                OdsPackageResourcePK oprpk = new OdsPackageResourcePK();                
                oprpk.setPackageId(odsPackageVersion.getId().getPackageId());
                oprpk.setPackageVer(odsPackageVersion.getId().getVer());
                oprpk.setResourceId(opd.getResourceId());
                oprpk.setResourceVer(Integer.parseInt(opd.getResourceVer()));
                opr.setId(oprpk);
                opr.setCreated(date);
                opr.setUpdated(date);
                opr.setCreateUserId(userId);
                opr.setUpdateUserId(userId);
                odsPackageResourceRepository.create(opr);
                //odsPackageResourceRepository.insertPackageResource(opr);
            }
        }
        //新增OdsPackageLayout
        if (!odsPackageLayoutList.isEmpty()) {
            for (OdsPackageLayout opl : odsPackageLayoutList) {
                OdsPackageLayout opltmp = new OdsPackageLayout();
                OdsPackageLayoutPK oplpktmp = new OdsPackageLayoutPK();                
                oplpktmp.setPackageId(odsPackageVersion.getId().getPackageId());
                oplpktmp.setPackageVer(odsPackageVersion.getId().getVer());
                oplpktmp.setRowPosition(opl.getId().getRowPosition());
                oplpktmp.setColumnPosition(opl.getId().getColumnPosition());
                opltmp.setId(oplpktmp);
                opltmp.setResourceId(opl.getResourceId());
                opltmp.setResourceVer(opl.getResourceVer());
                opltmp.setDescription(opl.getDescription());
                opltmp.setCreated(date);
                opltmp.setUpdated(date);
                opltmp.setCreateUserId(userId);
                opltmp.setUpdateUserId(userId);
                odsPackageLayoutRepository.create(opltmp);
                //odsPackageLayoutRepository.insertPackageLayout(opltmp);                
            }
        }
    }
    
    public List<Ods703eTab2DialogDto> findCommonRes() {
        return odsResourceRepository.findCommonRes();
    }

}
