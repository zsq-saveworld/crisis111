package com.jvlei.modelrouter.dicom;

import com.pixelmed.network.AbstractSyntaxSelectionPolicy;
import com.pixelmed.network.AnyExplicitStorePresentationContextSelectionPolicy;
import com.pixelmed.network.CompositeInstanceStoreAbstractSyntaxSelectionPolicy;
import com.pixelmed.network.PresentationContext;
import com.pixelmed.network.PresentationContextSelectionPolicy;
import com.pixelmed.network.TransferSyntaxSelectionPolicy;
import com.pixelmed.slf4j.Logger;
import com.pixelmed.slf4j.LoggerFactory;

import java.util.LinkedList;

public class AllPresentationContextSelectionPolicy implements PresentationContextSelectionPolicy {

    private static final Logger slf4jlogger = LoggerFactory.getLogger(AnyExplicitStorePresentationContextSelectionPolicy.class);

    protected AbstractSyntaxSelectionPolicy abstractSyntaxSelectionPolicy;
    protected TransferSyntaxSelectionPolicy transferSyntaxSelectionPolicy;

    public AllPresentationContextSelectionPolicy() {
        abstractSyntaxSelectionPolicy = new CompositeInstanceStoreAbstractSyntaxSelectionPolicy();
        transferSyntaxSelectionPolicy = new AllTransferSyntaxSelectionPolicy();
    }

    /**
     * Accept or reject Abstract Syntaxes (SOP Classes).
     *
     * Only SOP Classes for storage of composite instances and verification SOP Classes are accepted.
     *
     * @deprecated						SLF4J is now used instead of debugLevel parameters to control debugging - use {@link #applyPresentationContextSelectionPolicy(LinkedList,int)} instead.
     * @param	presentationContexts	a java.util.LinkedList of {@link PresentationContext PresentationContext} objects, each of which contains an Abstract Syntax (SOP Class UID) with one or more Transfer Syntaxes
     * @param	associationNumber		for debugging messages
     * @param	debugLevel				ignored
     * @return							the java.util.LinkedList of {@link PresentationContext PresentationContext} objects, as supplied but with the result/reason field set to either "acceptance" or "abstract syntax not supported (provider rejection)" or "transfer syntaxes not supported (provider rejection)" or " no reason (provider rejection)"
     */
    public LinkedList applyPresentationContextSelectionPolicy(LinkedList presentationContexts,int associationNumber,int debugLevel) {
        slf4jlogger.warn("Debug level supplied as argument ignored");
        return applyPresentationContextSelectionPolicy(presentationContexts,associationNumber);
    }

    /**
     * Accept or reject Abstract Syntaxes (SOP Classes).
     *
     * Only SOP Classes for storage of composite instances and verification SOP Classes are accepted.
     *
     * @param	presentationContexts	a java.util.LinkedList of {@link PresentationContext PresentationContext} objects, each of which contains an Abstract Syntax (SOP Class UID) with one or more Transfer Syntaxes
     * @param	associationNumber		for debugging messages
     * @return							the java.util.LinkedList of {@link PresentationContext PresentationContext} objects, as supplied but with the result/reason field set to either "acceptance" or "abstract syntax not supported (provider rejection)" or "transfer syntaxes not supported (provider rejection)" or " no reason (provider rejection)"
     */
    public LinkedList applyPresentationContextSelectionPolicy(LinkedList presentationContexts,int associationNumber) {
        if (slf4jlogger.isTraceEnabled()) {
            slf4jlogger.trace("Association[{}]: Presentation contexts requested:\n{}",associationNumber,presentationContexts.toString());
        }
        // must be called 1st
        presentationContexts = abstractSyntaxSelectionPolicy.applyAbstractSyntaxSelectionPolicy(presentationContexts, associationNumber);
        if (slf4jlogger.isTraceEnabled()) {
            slf4jlogger.trace("Association[{}]: Presentation contexts after applyAbstractSyntaxSelectionPolicy:\n{}",associationNumber,presentationContexts.toString());
        }
        // must be called 2nd
        presentationContexts = transferSyntaxSelectionPolicy.applyTransferSyntaxSelectionPolicy(presentationContexts,associationNumber);
        if (slf4jlogger.isTraceEnabled()) {
            slf4jlogger.trace("Association[{}]: Presentation contexts after applyTransferSyntaxSelectionPolicy:\n{}",associationNumber,presentationContexts.toString());
        }
        // must be called 3rd
        presentationContexts = transferSyntaxSelectionPolicy.applyExplicitTransferSyntaxPreferencePolicy(presentationContexts,associationNumber);
        if (slf4jlogger.isTraceEnabled()) {
            slf4jlogger.trace("Association[{}]: Presentation contexts after applyExplicitTransferSyntaxPreferencePolicy:\n{}",associationNumber,presentationContexts.toString());
        }
        return presentationContexts;
    }
}
