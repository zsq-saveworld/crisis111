package com.jvlei.modelrouter.dicom;

import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.network.AnyExplicitTransferSyntaxSelectionPolicy;
import com.pixelmed.network.PresentationContext;
import com.pixelmed.network.TransferSyntaxSelectionPolicy;
import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class AllTransferSyntaxSelectionPolicy extends TransferSyntaxSelectionPolicy {

    private static final Logger slf4jlogger = LoggerFactory.getLogger(AllTransferSyntaxSelectionPolicy.class);

    /**
     * Accept or reject Presentation Contexts, preferring Explicit over Implicit VR.
     * <p>
     * Should be called after Abstract Syntax selection has been performed.
     * <p>
     * Should be called before
     * {@link com.pixelmed.network.TransferSyntaxSelectionPolicy#applyExplicitTransferSyntaxPreferencePolicy(LinkedList, int)
     * applyExplicitTransferSyntaxPreferencePolicy()}.
     * <p>
     * Does not change the Abstract Syntax.
     *
     * @deprecated SLF4J is now used instead of debugLevel parameters to control debugging - use
     * {@link #applyTransferSyntaxSelectionPolicy(LinkedList, int)} instead.
     * @param    presentationContexts    a java.util.LinkedList of {@link PresentationContext PresentationContext} objects, each of which contains an
     * Abstract Syntax (SOP Class UID) with one or more Transfer Syntaxes
     * @param    associationNumber        used for debugging messages
     * @param    debugLevel                ignored
     * @return the java.util.LinkedList of {@link PresentationContext PresentationContext} objects, as supplied but with the Transfer Syntax
     * list culled to the one preferred Transfer Syntax (or empty if none acceptable) and the result/reason field left alone if one of the Transfer
     * Syntaxes was acceptable, or set to "transfer syntaxes not supported (provider rejection)"
     */
    public LinkedList applyTransferSyntaxSelectionPolicy(LinkedList presentationContexts, int associationNumber, int debugLevel) {
        slf4jlogger.warn("Debug level supplied as argument ignored");
        return applyTransferSyntaxSelectionPolicy(presentationContexts, associationNumber);
    }

    /**
     * Accept or reject Presentation Contexts, preferring Explicit over Implicit VR.
     * <p>
     * Should be called after Abstract Syntax selection has been performed.
     * <p>
     * Should be called before
     * {@link com.pixelmed.network.TransferSyntaxSelectionPolicy#applyExplicitTransferSyntaxPreferencePolicy(LinkedList, int)
     * applyExplicitTransferSyntaxPreferencePolicy()}.
     * <p>
     * Does not change the Abstract Syntax.
     *
     * @param    presentationContexts    a java.util.LinkedList of {@link PresentationContext PresentationContext} objects, each of which contains an
     * Abstract Syntax (SOP Class UID) with one or more Transfer Syntaxes
     * @param    associationNumber        used for debugging messages
     * @return the java.util.LinkedList of {@link PresentationContext PresentationContext} objects, as supplied but with the Transfer Syntax
     * list culled to the one preferred Transfer Syntax (or empty if none acceptable) and the result/reason field left alone if one of the Transfer
     * Syntaxes was acceptable, or set to "transfer syntaxes not supported (provider rejection)"
     */
    public LinkedList applyTransferSyntaxSelectionPolicy(LinkedList presentationContexts, int associationNumber) {
        ListIterator pcsi = presentationContexts.listIterator();
        while (pcsi.hasNext()) {
            PresentationContext pc = (PresentationContext) (pcsi.next());
            boolean foundImplicitVRLittleEndian = false;
            List tsuids = pc.getTransferSyntaxUIDs();
            // discard old list and make a new one ...
            pc.newTransferSyntaxUIDs();
            boolean addedOne = false;
            ListIterator tsuidsi = tsuids.listIterator();
            while (tsuidsi.hasNext()) {
                String transferSyntaxUID = (String) (tsuidsi.next());
                if (transferSyntaxUID != null) {
                    if (transferSyntaxUID.equals(TransferSyntax.ImplicitVRLittleEndian)) {
                        foundImplicitVRLittleEndian = true;
                    } else {
                        TransferSyntax ts = new TransferSyntax(transferSyntaxUID);
                        if (ts.isRecognized() && ts.isExplicitVR()) {
                            pc.addTransferSyntaxUID(transferSyntaxUID);
                            addedOne = true;
                        }
                    }
                }
            }
            if (!addedOne) {
                if (foundImplicitVRLittleEndian) {
                    pc.addTransferSyntaxUID(TransferSyntax.ImplicitVRLittleEndian);
                } else {
                    pc.setResultReason((byte) 4);                // transfer syntaxes not supported (provider rejection)
                }
            }
        }
        return presentationContexts;
    }

    public LinkedList applyExplicitTransferSyntaxPreferencePolicy(LinkedList presentationContexts,int associationNumber) {
        //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: start");
        // Objective is to cull list so that we make the choice of
        // explicit over implicit if more than one TS offered and accepted for the same AS

        HashSet allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax = new HashSet();

        // Pass 1 - fill allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax

        //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: start pass 1");
        ListIterator pcsi = presentationContexts.listIterator();
        while (pcsi.hasNext()) {
            //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: iterating");
            PresentationContext pc = (PresentationContext)(pcsi.next());
            //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: have pc "+pc);
            String transferSyntaxUID=pc.getTransferSyntaxUID();		// There will only be one by this time
            //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: have transferSyntaxUID "+transferSyntaxUID);
            if (transferSyntaxUID != null && TransferSyntax.isExplicitVR(transferSyntaxUID)) {
                //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: adding to allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax: "+pc);
                allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax.add(pc.getAbstractSyntaxUID());
            }
        }

        // Pass 2 - reject any PC with an IVR for an AS that is in allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax

        //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: start pass 2");
        // todo:
        /*pcsi = presentationContexts.listIterator();
        while (pcsi.hasNext()) {
            PresentationContext pc = (PresentationContext)(pcsi.next());
            String transferSyntaxUID=pc.getTransferSyntaxUID();		// There will only be one by this time
            if (transferSyntaxUID != null
                    && TransferSyntax.isImplicitVR(transferSyntaxUID)
                    && allAbstractSyntaxesAcceptedWithExplicitVRTransferSyntax.contains(pc.getAbstractSyntaxUID())) {
                //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: rejecting: "+pc);
                pc.setResultReason((byte)2);				// no reason (provider rejection)
            }
        }*/
        //System.err.println("applyExplicitTransferSyntaxPreferencePolicy: done");
        return presentationContexts;
    }
}
