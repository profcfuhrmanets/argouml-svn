// Copyright (c) 1996-2002 The Regents of the University of California. All
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

// $Id$
package org.argouml.uml.ui.foundation.core;

import java.util.HashMap;
import java.util.Map;

import org.argouml.application.api.Argo;
import org.argouml.uml.ui.UMLRadioButtonPanel;

import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.data_types.MChangeableKind;

/**
 * 
 * @author jaap.branderhorst@xs4all.nl	
 * @since Jan 4, 2003
 */
public class UMLAssociationEndChangeabilityRadioButtonPanel extends UMLRadioButtonPanel {

    private static Map labelTextsAndActionCommands = new HashMap();

    static {
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-addonly"), ActionSetAssociationEndChangeability.ADDONLY_COMMAND);
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-changeable"), ActionSetAssociationEndChangeability.CHANGEABLE_COMMAND);
        labelTextsAndActionCommands.put(Argo.localize("UMLMenu", "label.changeability-frozen"), ActionSetAssociationEndChangeability.FROZEN_COMMAND);
    }

    /**
     * Constructor for UMLAssociationEndChangeabilityRadioButtonPanel.
     * @param title
     * @param labeltexts
     * @param propertySetName
     * @param setAction
     * @param horizontal
     */
    public UMLAssociationEndChangeabilityRadioButtonPanel(String title, boolean horizontal) {
        super(title, labelTextsAndActionCommands, "changeability", ActionSetAssociationEndChangeability.SINGLETON, horizontal);
    }

    /**
     * @see org.argouml.uml.ui.UMLRadioButtonPanel#buildModel()
     */
    public void buildModel() {
        if (getTarget() != null) {
            MAssociationEnd target = (MAssociationEnd)getTarget();
            MChangeableKind kind = target.getChangeability();
            if (kind == null || kind.equals(MChangeableKind.CHANGEABLE)) {
                setSelected(ActionSetAssociationEndChangeability.CHANGEABLE_COMMAND);
            } else
            if (kind.equals(MChangeableKind.ADD_ONLY)) {
                setSelected(ActionSetAssociationEndChangeability.ADDONLY_COMMAND); 
            } else
            if (kind.equals(MChangeableKind.FROZEN)) {
                setSelected(ActionSetAssociationEndChangeability.FROZEN_COMMAND);
            } else
                setSelected(ActionSetAssociationEndChangeability.CHANGEABLE_COMMAND);
        }
    }
}
