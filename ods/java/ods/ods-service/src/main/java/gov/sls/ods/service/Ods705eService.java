package gov.sls.ods.service;

import gov.sls.commons.core.io.FileStore;
import gov.sls.commons.core.io.Locations;
import gov.sls.commons.core.security.user.UserHolder;
import gov.sls.entity.ods.OdsLayout;
import gov.sls.ods.config.OdsApplicationProperties;
import gov.sls.ods.config.OdsApplicationPropertiesAccessor;
import gov.sls.ods.io.OdsLocations;
import gov.sls.ods.repository.OdsLayoutRepository;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cht.commons.persistence.query.SqlExecutor;
import com.google.common.base.Strings;

@Slf4j
@Service
public class Ods705eService {

    @Autowired
    private OdsLayoutRepository OdsLayoutRepository;
    
    @Autowired
    private SqlExecutor sqlExe;
    
    @Inject
    private OdsApplicationPropertiesAccessor propertiesAccessor;
    
    @Autowired
    private ApplicationContext ap;
    
    @Autowired
    FileStore fileStore;

    public List<OdsLayout> findByName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return OdsLayoutRepository.findByNameLike("%" + name + "%");
        }        
        return OdsLayoutRepository.findAll();
    }

    public void save(OdsLayout odsLayout) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        odsLayout.setUpdated(date);
        odsLayout.setUpdateUserId(userId);
        OdsLayoutRepository.save(odsLayout);
        //產生預覽圖
        // TODO 將基本圖放到service層
        try {
            String imgInPath = ap.getResource("images").getFile().getAbsolutePath();
            log.debug("PATH:" + imgInPath);
            String imgOutPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
            genPreviewImage(odsLayout.getId(), odsLayout.getPattern(), imgInPath, imgOutPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    public String create(OdsLayout odsLayout) {
        String userId = UserHolder.getUser().getId();
        Date date = new Date();
        //檢查名稱是否有重覆, 如果沒有才新增
        if (OdsLayoutRepository.findByName(odsLayout.getName()).isEmpty()) {
            OdsLayout ol = new OdsLayout();
            ol.setName(odsLayout.getName());
            ol.setPattern(odsLayout.getPattern());
            ol.setCreated(date);
            ol.setUpdated(date);
            ol.setCreateUserId(userId);
            ol.setUpdateUserId(userId);
            OdsLayoutRepository.create(ol);
            
            //產生預覽圖
            // TODO 將基本圖放到service層
            try {
                String imgInPath = ap.getResource("images").getFile().getAbsolutePath();
                log.debug("PATH:" + imgInPath);
                String imgOutPath = propertiesAccessor.getProperty(OdsApplicationProperties.ENVIRONMENT_PUBLIC_PATH);
                genPreviewImage(ol.getId(), ol.getPattern(), imgInPath, imgOutPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
            
            return "";
        } else {
            return "duplicated";
        }
    }

    public void delete(OdsLayout odsLayout) {
        OdsLayoutRepository.delete(odsLayout.getId());
    }

    private void genPreviewImage(String id, String pattern, String imgInPath, String imgOutPath) throws IOException {
        // TODO Auto-generated method stub
        String[] pa = pattern.split(",");
        int rows = pa.length;   //列  
        int cols = 1;           //只有1欄
        int chunks = rows * cols;
  
        int chunkWidth, chunkHeight;  
        int type;  
        //fetching image files 
        /*
        File[] imgFiles = new File[chunks];
        //InputStream[] imgFiles = new InputStream[chunks];
        
        for (int i = 0; i < chunks; i++) {
            imgFiles[i] = new File(imgInPath + "/column" + pa[i] + "Pv.jpg");            
//            log.debug(ImageUtil.class.getResource("").getPath());
//            imgFiles[i] = new File(imgPath+"").ImageUtil.class.getClass().
//                    getResourceAsStream("/ods/images/column" + pa[i] + "Pv.jpg");            
        }
        */ 
      
       //creating a bufferd image array from image files  
        BufferedImage[] buffImages = new BufferedImage[chunks];  
        for (int i = 0; i < chunks; i++) {  
            //buffImages[i] = ImageIO.read(fileStore.getFile(Locations.Persistent.ROOT, imgInPath + "/column" + pa[i] + "Pv.jpg"));
            buffImages[i] = ImageIO.read(fileStore.getFile(OdsLocations.Persistent.IMG_IN_PATH_ROOT, imgInPath + "/column" + pa[i] + "Pv.jpg"));            
            //log.debug("imgOutPath:" + OdsLocations.Persistent.IMG_IN_PATH_ROOT + imgInPath + "/column" + pa[i] + "Pv.jpg");
            //buffImages[i] = ImageIO.read(new File(imgInPath + "/column" + pa[i] + "Pv.jpg"));
        }
        type = buffImages[0].getType();  
        chunkWidth = buffImages[0].getWidth();  
        chunkHeight = buffImages[0].getHeight();
        
        //creating stripe image
        BufferedImage stripe = ImageIO.read(fileStore.getFile(OdsLocations.Persistent.IMG_IN_PATH_ROOT, imgInPath + "/stripe.jpg"));
        //BufferedImage stripe = ImageIO.read(new File(imgInPath + "/stripe.jpg"));
//        BufferedImage stripe = ImageIO.read(ImageUtil.class.getClass().getClassLoader().
//                getResourceAsStream("/images" + File.separator + "stripe.jpg"));
        int stripeHeight = 14;
  
        //Initializing the final image  
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows + stripeHeight * (rows + 1), type);  
  
        int num = 0;  
        for (int i = 0; i < rows; i++) {  
            for (int j = 0; j < cols; j++) {
                if (i == 0 && j == 0) {
                    finalImg.createGraphics().drawImage(stripe, 0, 0, null); 
                }
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i + stripeHeight * ( i + 1), null);
                finalImg.createGraphics().drawImage(stripe, chunkWidth * j, chunkHeight * (i + 1) + stripeHeight * ( i + 1), null);
                num++;  
            }  
        }  
        //System.out.println("Image concatenated.....");  
        log.debug("AAA"+imgOutPath);
        File imgFile = fileStore.getFile(Locations.Persistent.ROOT, imgOutPath + "layout" + File.separator + id + File.separator + "image" + File.separator + id +".png");
        //File imgFile = new File(imgOutPath + "layout" + File.separator + id + File.separator + "image" + File.separator + id +".png");
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }
        ImageIO.write(finalImg, "jpeg", imgFile);  

    }
}
