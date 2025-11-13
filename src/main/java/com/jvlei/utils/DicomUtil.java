package com.jvlei.utils;

import cn.hutool.core.io.FileUtil;
import com.jvlei.modelrouter.dicom.AllPresentationContextSelectionPolicy;
import com.jvlei.modelrouter.dicom.CFindHandler;
import com.jvlei.modelrouter.dicom.CGetHandler;
import com.jvlei.modelrouter.dicom.CMoveHandler;
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
import com.pixelmed.network.AnyExplicitStoreFindMoveGetPresentationContextSelectionPolicy;
import com.pixelmed.network.AnyExplicitStorePresentationContextSelectionPolicy;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.MoveSOPClassSCU;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;
import io.jsonwebtoken.lang.Strings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Data
public class DicomUtil implements DisposableBean {

    public static String storeScpAeTitle = System.getProperty("jvlei.dicom.storescp.aeTitle", "localAe");

    private static boolean enableStoreScp = Boolean.getBoolean("jvlei.dicom.storescp.enableStoreScp");

    private static int storeScpPortNumber = Integer.getInteger("jvlei.dicom.storescp.portNumber", 11112);

    private static String dir = System.getProperty("jvlei.dicom.storescp.dir", "d:/testpage/TECHTEST/qrdata");

    private static DicomUtil instance = new DicomUtil();

    private Thread thread;

    private String defaultCrisisInputFileDir = System.getProperty("jvlei.ai.crisis.inputFileRoot", "d:/testpage/TECHTEST/crisis");

    private DicomUtil() {
        log.info("start to initialize instance.");
        try {
            if (!enableStoreScp) {
                return;
            }
            if (!FileUtil.exist(dir)) {
                FileUtil.mkdir(dir);
            }
            thread = new Thread(new StorageSOPClassSCPDispatcher(storeScpPortNumber, storeScpAeTitle, new File(dir),
                    StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER, new CMoveHandler(defaultCrisisInputFileDir), null, null, null,
                    // AnyExplicitStoreFindMoveGetPresentationContextSelectionPolicy
                    new AllPresentationContextSelectionPolicy(), false));

            /*thread = new Thread(new StorageSOPClassSCPDispatcher(storeScpPortNumber, storeScpAeTitle, new File(dir),
                    StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER, new CMoveHandler(defaultCrisisInputFileDir)));*/

            FileUtil.mkParentDirs(dir);
            thread.start();
        } catch (IOException e) {
            log.warn("failed to listen store sop dispatcher", e);
            throw new RuntimeException(e);
        }
    }

    public static void store(String scpAe, int scpPort, String scpIp, String localAe, String accnum, String patientId,
            Consumer<AttributeList> consumer) {

    }


    public static String getSeries(String scpAe, int scpPort, String scpIp, String localAe, String accnum, String patientId,
            Consumer<AttributeList> consumer) {
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

            new FindSOPClassSCU(scpIp, scpPort, scpAe, localAe, SOPClass.StudyRootQueryRetrieveInformationModelFind, identifier,
                    new CFindHandler(consumer));
        } catch (DicomException | IOException | DicomNetworkException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String getStudy(String scpAe, int scpPort, String scpIp, String localAe, String accnum, String patientId,
            Consumer<AttributeList> consumer) {
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
            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.Modality);
            identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet);
            if (Strings.hasText(patientId)) {
                identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet).addValue(patientId);
            } else {
                identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
            }
            identifier.putNewAttribute(TagFromName.AccessionNumber).addValue(accnum);

            new FindSOPClassSCU(scpIp, scpPort, scpAe, localAe, SOPClass.StudyRootQueryRetrieveInformationModelFind, identifier,
                    new CFindHandler(consumer));
        } catch (DicomException | IOException | DicomNetworkException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static String getSeries(String studyIuid, String scpAe, int scpPort, String scpIp, String localAe, String accnum, String patientId,
            String seriesDescription, Consumer<AttributeList> consumer) {
        if (!StringUtils.hasText(accnum)) {
            log.warn("accnum can't be null.");
            return null;
        }
        // use the default character set for VR encoding - override this as necessary
        SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet((String[]) null);
        AttributeList identifier = new AttributeList();
        try {
            // build the attributes that you would like to retrieve as well as passing in any search criteria specific query root
            identifier.putNewAttribute(TagFromName.StudyInstanceUID).addValue(studyIuid);
            identifier.putNewAttribute(TagFromName.QueryRetrieveLevel).addValue("SERIES");
            identifier.putNewAttribute(TagFromName.SOPInstanceUID);
            identifier.putNewAttribute(TagFromName.StudyDescription);
            identifier.putNewAttribute(TagFromName.SOPClassesInStudy);
            if (seriesDescription != null) {
                identifier.putNewAttribute(TagFromName.SeriesDescription).addValue(seriesDescription);
            }
            identifier.putNewAttribute(TagFromName.SeriesDescriptionCodeSequence);
            identifier.putNewAttribute(TagFromName.RelatedSeriesSequence);
            identifier.putNewAttribute(TagFromName.ReferencedSeriesSequence);

            identifier.putNewAttribute(TagFromName.ReferencedSeriesSequence);

            identifier.putNewAttribute(TagFromName.StudyDate);
            identifier.putNewAttribute(TagFromName.Modality);
            // identifier.putNewAttribute(TagFromName.PatientName, specificCharacterSet);
            identifier.putNewAttribute(TagFromName.PatientName);
            if (Strings.hasText(patientId)) {
                identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet).addValue(patientId);
            } else {
                // identifier.putNewAttribute(TagFromName.PatientID, specificCharacterSet);
                identifier.putNewAttribute(TagFromName.PatientID);
            }
            identifier.putNewAttribute(TagFromName.AccessionNumber).addValue(accnum);

            new FindSOPClassSCU(scpIp, scpPort, scpAe, localAe, SOPClass.StudyRootQueryRetrieveInformationModelFind, identifier,
                    new CFindHandler(consumer));
        } catch (DicomException | IOException | DicomNetworkException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void getImage(String scpAe, int scpPort, String scpIp, String localAe, String imageStorePath, String studyIuid, String accnum,
            Set<String> sop) {
        String storePath = imageStorePath + File.separator + accnum;
        if (!FileUtil.exist(storePath)) {
            FileUtil.mkParentDirs(storePath);
        }
        File pathToStoreIncomingDicomFiles = new File(storePath);

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
                attribute.addValue(studyIuid);
                identifier.put(tag, attribute);
            }
            new GetSOPClassSCU(scpIp, scpPort, scpAe, localAe, SOPClass.StudyRootQueryRetrieveInformationModelGet, identifier,
                    new IdentifierHandler(), pathToStoreIncomingDicomFiles, StoredFilePathStrategy.BYSOPINSTANCEUIDINSINGLEFOLDER, new CGetHandler(),
                    sop, 0, true, false, false);

        } catch (Exception e) {
            log.error("Error during get operation", e);
        }
    }

    public static void moveImageBySeries(String scpAe, int scpPort, String scpIp, String localAe, String storeScpAe, String studyIuid,
            String seriesIuid) {
        try {
            log.info("c-move bySeries task start. scpIp: {}, studyIuid: {}, seriesIuid: {}", scpIp, studyIuid, seriesIuid);
            AttributeList identifier = new AttributeList();
            {
                AttributeTag tag = TagFromName.QueryRetrieveLevel;
                Attribute attribute = new CodeStringAttribute(tag);
                attribute.addValue("SERIES");
                identifier.put(tag, attribute);
            }
            {
                AttributeTag tag = TagFromName.StudyInstanceUID;
                Attribute attribute = new UniqueIdentifierAttribute(tag);
                attribute.addValue(studyIuid);
                identifier.put(tag, attribute);
            }
            {
                AttributeTag tag = TagFromName.SeriesInstanceUID;
                Attribute attribute = new UniqueIdentifierAttribute(tag);
                attribute.addValue(seriesIuid);
                identifier.put(tag, attribute);
            }
            new MoveSOPClassSCU(scpIp, scpPort, scpAe, localAe, storeScpAe, SOPClass.StudyRootQueryRetrieveInformationModelMove, identifier);
            log.info("c-move bySeries task finished. scpIp: {}, studyIuid: {}, seriesIuid: {}", scpIp, studyIuid, seriesIuid);

        } catch (Exception e) {
            log.error("Error during move operation", e);
        }
    }

    public static void getSeriesImg(String accessionNum, String remoteAe, int remotePort, String remoteIp, String localAe, String patientId,
            String seriesDescription) {
        DicomUtil.getStudy(remoteAe, remotePort, remoteIp, localAe, accessionNum, patientId, (list) -> {
            String studyInstanceUid = list.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
            Set<String> setofSopClassesExpected = new HashSet<>();
            Attribute sopClassesInStudy = list.get(TagFromName.SOPClassesInStudy);
            if (sopClassesInStudy != null) {
                String[] sopClassesInStudyList = null;
                try {
                    sopClassesInStudyList = sopClassesInStudy.getStringValues();
                } catch (DicomException e) {
                    // do nothing.
                }
                if (sopClassesInStudyList != null) {
                    setofSopClassesExpected.addAll(Arrays.asList(sopClassesInStudyList));
                }
            }
            if (CollectionUtils.isEmpty(setofSopClassesExpected)) {
                //if SOP class data for study is not found, then supply all storage SOP classes
                //todo: 无法从DICOM中获取到检查类别以及相关的SOPClassUID 暂只支持CT,MR,CR,RF
                setofSopClassesExpected.add("1.2.840.10008.5.1.4.1.1.2");
            }

            DicomUtil.getSeries(studyInstanceUid, remoteAe, remotePort, remoteIp, localAe, accessionNum, patientId, seriesDescription, (item) -> {
                String seriesIuid = item.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrEmptyString();
                log.info("start to move series image, seriesIuid: {}", seriesIuid);
                DicomUtil.moveImageBySeries(remoteAe, remotePort, remoteIp, localAe, DicomUtil.storeScpAeTitle, studyInstanceUid, seriesIuid);
            });
        });
    }

    @Override
    public void destroy() {
        if (thread != null) {
            thread.interrupt();
        }
    }
}
