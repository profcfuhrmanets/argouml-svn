// $Id$
// Copyright (c) 1996-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.uml.ui.foundation.core;

import org.argouml.i18n.Translator;
import org.argouml.uml.ui.ActionNavigateNamespace;
import org.argouml.uml.ui.ActionRemoveFromModel;
import org.argouml.uml.ui.PropPanelButton2;
import org.argouml.uml.ui.UMLLinkedList;
import org.argouml.uml.ui.foundation.extension_mechanisms.ActionNewStereotype;
import org.argouml.util.ConfigLoader;

import javax.swing.*;

/**
 * PropPanel for a UML component.<p>
 *
 * TODO: this property panel needs refactoring to remove dependency on
 *       old gui components.
 *
 * @author 5eichler@informatik.uni-hamburg.de
 */
public class PropPanelComponent extends PropPanelClassifier {

    /**
     * The constructor.
     *
     */
    public PropPanelComponent() {
        super("Component", ConfigLoader.getTabPropsOrientation());
        addField(Translator.localize("label.name"),
                getNameTextField());
        addField(Translator.localize("label.stereotype"),
                getStereotypeSelector());
        addField(Translator.localize("label.namespace"),
                getNamespaceSelector());
        add(getModifiersPanel());

        addSeperator();

        addField(Translator.localize("label.generalizations"),
                getGeneralizationScroll());
        addField(Translator.localize("label.specializations"),
                getSpecializationScroll());

        addSeperator();

        addField(Translator.localize("label.client-dependencies"),
                getClientDependencyScroll());
        addField(Translator.localize("label.supplier-dependencies"),
                getSupplierDependencyScroll());

        JList resList = new UMLLinkedList(new UMLComponentResidentListModel());
        addField(Translator.localize("label.residents"),
                new JScrollPane(resList));

        addButton(new PropPanelButton2(new ActionNavigateNamespace()));
        addButton(new PropPanelButton2(getActionNewReception(),
                lookupIcon("Reception")));
        addButton(new PropPanelButton2(new ActionNewStereotype(),
                lookupIcon("Stereotype")));
        addButton(new PropPanelButton2(new ActionRemoveFromModel(),
                lookupIcon("Delete")));

        //    addCaption(Translator.localize("label.name"),1,0,0);
        //    addField(getNameTextField(),1,0,0);
        //
        //    addCaption(Translator.localize("label.stereotype"),
        //        2,0,0);
        //    addField(getStereotypeBox(),2,0,0);
        //
        //    addCaption(Translator.localize("label.namespace"),
        //        3,0,0);
        //    addField(getNamespaceComboBox(),3,0,0);
        //
        //    addCaption(Translator.localize("label.modifiers"),
        //        4,0,1);
        //    JPanel modifiersPanel = new JPanel(new GridLayout(0,3));
        //    modifiersPanel.add(new UMLCheckBox(Translator.localize(
        //        "checkbox.abstract-lc"),this,new UMLReflectionBooleanProperty(
        //        "isAbstract",Model.getFacade().COMPONENT,"isAbstract",
        //        "setAbstract")));
        //    modifiersPanel.add(new UMLCheckBox(Translator.localize(
        //        "checkbox.final-lc"),this,new UMLReflectionBooleanProperty(
        //        "isLeaf",Model.getFacade().COMPONENT,"isLeaf","setLeaf")));
        //    modifiersPanel.add(new UMLCheckBox(localize("root"),this,
        //        new UMLReflectionBooleanProperty("isRoot",
        //                                         Model.getFacade().COMPONENT,
        //                                         "isRoot",
        //                                         "setRoot")));
        //    addField(modifiersPanel,4,0,0);
        //
        //    addCaption("Generalizations:",0,1,1);
        //    addField(getGeneralizationScroll(),0,1,1);
        //
        //    addCaption("Specializations:",1,1,1);
        //    addField(getSpecializationScroll(),1,1,1);
        //
        //    new PropPanelButton(this,buttonPanel,_navUpIcon,
        //        Translator.localize("button.go-up"),
        //        "navigateUp",null);
        //    new PropPanelButton(this,buttonPanel,_deleteIcon,localize(
        //        "Delete component"),"removeElement",null);
    }


} /* end class PropPanelComponent */


