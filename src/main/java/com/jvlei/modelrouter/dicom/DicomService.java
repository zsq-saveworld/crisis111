package com.jvlei.modelrouter.dicom;

import cn.hutool.core.io.FileUtil;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dcm4che3.net.service.QueryRetrieveLevel2;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Data
@Slf4j
public class DicomService {

    private DicomConfig dicomConfig;

    private CFindHandler cFindHandler;

    public DicomService(DicomConfig dicomConfig, CFindHandler cFindHandler) {
        this.dicomConfig = dicomConfig;
        this.cFindHandler = cFindHandler;
    }

    /**
     * 获取序列信息
     * @param accnum
     * @param patientId
     * @return
     */
    public String getSeries(String accnum, String patientId, Consumer<AttributeList> consumer) {
        if (!StringUtils.hasText(accnum)) {
            log.warn("accnum can't be null.");
            return null;
        }
        // use the default character set for VR encoding - override this as necessary
        SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
        AttributeList identifier = new AttributeList();
        try {
            // build the attributes that you would like to retrieve as well as passing in any search criteria specific query root
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("STUDY");
            identifier.putNewAttribute(TagFromName.StudyInstanceUID);
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.SOPClassesInStudy);
            identifier.putNewAttribute(TagFromName.SeriesDescription);
            identifier.putNewAttribute(TagFromName.SeriesDescriptionCodeSequence);
            identifier.putNewAttribute(TagFromName.RelatedSeriesSequence);
            identifier.putNewAttribute(TagFromName.ReferencedSeriesSequence);

            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.Modality);
            identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet);
            if (Strings.hasText(patientId)) {
                identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet).addValue(patientId);
            } else {
                identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
            }
            identifier.putNewAttribute(TagFromName.AccessionNumber).addValue(accnum);

            new FindSOPClassSCU(dicomConfig.getScpIp(), dicomConfig.getScpPort(), dicomConfig.getScpAe(),
                    dicomConfig.getLocalAe(),
                    SOPClass.StudyRootQueryRetrieveInformationModelFind,
                    identifier,
                    new CFindHandler(consumer));
        } catch (DicomException | IOException | DicomNetworkException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getImage(AttributeList attributeListForFindResult,String accNum, String modality) throws DicomException {
        String storePath = dicomConfig.getStorePath() + File.separator + accNum;
        if (!FileUtil.exist(storePath)) {
            FileUtil.mkParentDirs(storePath);
        }
        File pathToStoreIncomingDicomFiles = new File(storePath);


        log.info("Matched result:" + attributeListForFindResult);

        String studyInstanceUID = attributeListForFindResult.get(TagFromName.StudyInstanceUID)
                .getSingleStringValueOrEmptyString();
        log.info("studyInstanceUID of matched result:" + studyInstanceUID);

        Set<String> setofSopClassesExpected = new HashSet<String>();
        Attribute sopClassesInStudy = attributeListForFindResult.get(TagFromName.SOPClassesInStudy);
        if (sopClassesInStudy != null) {
            String[] sopClassesInStudyList = sopClassesInStudy.getStringValues();
            for (String sopClassInStudy : sopClassesInStudyList) {
                setofSopClassesExpected.add(sopClassInStudy);
            }
        } else {
            //if SOP class data for study is not found, then supply all storage SOP classes
            //todo: 无法从DICOM中获取到检查类别以及相关的SOPClassUID 暂只支持CT,MR,CR,RF
            setofSopClassesExpected.add("1.2.840.10008.5.1.4.1.1.2");
        }

        try {

            AttributeList identifier = new AttributeList();
            {
                AttributeTag tag = TagFromName.QueryRetrieveLevel;
                Attribute attribute = new CodeStringAttribute(tag);
                attribute.addValue("STUDY");
                identifier.put(tag, attribute);
            }
            {
                AttributeTag tag = TagFromName.StudyInstanceUID;
                Attribute attribute = new UniqueIdentifierAttribute(tag);
                attribute.addValue(studyInstanceUID);
                identifier.put(tag, attribute);
            }

            //please see PixelMed documentation if you want to dig deeper into the parameters and their relevance
            new GetSOPClassSCU(dicomConfig.getScpIp(), dicomConfig.getScpPort(), dicomConfig.getScpAe(),
                    dicomConfig.getLocalAe(),
                    SOPClass.StudyRootQueryRetrieveInformationModelGet,
                    identifier,
                    new IdentifierHandler(), //override and provide your own handler if you need to do anything else
                    pathToStoreIncomingDicomFiles,
                    StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER,
                    new CGetHandler(),
                    setofSopClassesExpected,
                    0,
                    true,
                    false,
                    false);

        } catch (Exception e) {
            log.error("Error during get operation", e); // in real life, do something about this exception
        }
        return null;
    }

}
