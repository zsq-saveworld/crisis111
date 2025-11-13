package com.jvlei.modelrouter.dicom;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.IdentifierHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class CFindHandler extends IdentifierHandler {

   private Consumer<AttributeList> consumer;

    public CFindHandler(Consumer<AttributeList> consumer) {
        this.consumer = consumer;
    }

    public CFindHandler() {

    }

    @Override
    public void doSomethingWithIdentifier(AttributeList attributeListForFindResult) throws DicomException {
        String studyInstanceUid = attributeListForFindResult.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
        log.info("studyInstanceUID of matched result:{}", studyInstanceUid);
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
            setofSopClassesExpected.add("dicomProperty.getSOPClassUID(modality)");
        }
        log.info("series desc: {}", attributeListForFindResult.get(TagFromName.SeriesDescription));

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
                attribute.addValue("studyInstanceUID");
                identifier.put(tag, attribute);
            }

            if (consumer != null) {
                consumer.accept(attributeListForFindResult);
            }

        } catch (Exception e) {
            log.error("Error during get operation", e);
        }
    }

}
