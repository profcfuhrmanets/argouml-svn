// $Id$
// Copyright (c) 1996-2004 The Regents of the University of California. All
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

package org.argouml.uml.diagram.static_structure.ui;

import javax.swing.Action;

import org.apache.log4j.Logger;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.uml.diagram.static_structure.ClassDiagramGraphModel;
import org.argouml.uml.diagram.ui.UMLDiagram;
import org.argouml.uml.diagram.ui.ActionAddAttribute;
import org.argouml.uml.diagram.ui.ActionAddOperation;
import org.tigris.gef.base.LayerPerspective;
import org.tigris.gef.base.LayerPerspectiveMutable;

/**
 * @author jrobbins@ics.uci.edy
 */
public class UMLClassDiagram extends UMLDiagram {

    private static final Logger LOG = Logger.getLogger(UMLClassDiagram.class);

    ////////////////
    // actions for toolbar
    private Action actionClass;
    private Action actionObject;
    private Action actionInterface;
    private Action actionDependency;
    private Action actionPermission;
    private Action actionUsage;
    private Action actionLink;
    private Action actionGeneralization;
    private Action actionRealization;
    private Action actionPackage;
    private Action actionModel;
    private Action actionSubsystem;
    private Action actionAssociation;
    private Action actionAggregation;
    private Action actionComposition;
    private Action actionUniAssociation;
    private Action actionUniAggregation;
    private Action actionUniComposition;
    private Action actionAttribute;
    private Action actionOperation;

    private static int classDiagramSerial = 1;

    /**
     * constructor
     */
    public UMLClassDiagram() {
        super();
    }

    /**
     * constructor
     * @param name the name for the new diagram 
     * @param namespace the namespace for the new diagram
     */
    public UMLClassDiagram(String name, Object namespace) {
        super(name, namespace);
    }

    /**
     * constructor
     * @param m the namespace
     */
    public UMLClassDiagram(Object m) {
        this(getNewDiagramName(), m);
    }

    /**
     * @see org.argouml.uml.diagram.ui.UMLDiagram#setNamespace(java.lang.Object)
     */
    public void setNamespace(Object handle) {
        if (!ModelFacade.isANamespace(handle)) {
            LOG.error("Illegal argument. "
                  + "Object " + handle + " is not a namespace");
            throw new IllegalArgumentException("Illegal argument. "
            			       + "Object " + handle
            			       + " is not a namespace");
        }
        Object m = /*(MNamespace)*/ handle;
        super.setNamespace(m);
        ClassDiagramGraphModel gm = new ClassDiagramGraphModel();
        gm.setNamespace(m);
        LayerPerspective lay =
            new LayerPerspectiveMutable(ModelFacade.getName(m), gm);
        ClassDiagramRenderer rend = new ClassDiagramRenderer(); // singleton
        lay.setGraphNodeRenderer(rend);
        lay.setGraphEdgeRenderer(rend);
        setLayer(lay);
    }

    /**
     * Get the actions from which to create a toolbar or equivilent
     * graphic trigger.
     *
     * @see org.argouml.uml.diagram.ui.UMLDiagram#getUmlActions()
     */
    protected Object[] getUmlActions() {
        Object actions[] = {
            getActionPackage(),
            getActionClass(),
            getAssociationActions(),
            getActionGeneralization(),
            null,
            getActionInterface(),
            getActionRealization(), 
            null,
            getDependencyActions(), 
            null,
            getActionAttribute(),
            getActionOperation()
        };

        return actions;
    }
    
    // To enable models and subsystems,
    // replace getActionPackage() in the function getUmlActions() above 
    // with getPackageActions().
    private Object[] getPackageActions() {
        Object actions[] = 
        { 
                getActionPackage(), 
                getActionModel(),
                getActionSubsystem() 
        };
        return actions;
    }
    
    /**
     * Return an array of dependency actions in the
     * pattern of which to build a popup toolbutton
     */
    private Object[] getDependencyActions() {
        Object actions[][] = {
            {getActionDependency()},
            {getActionPermission()},
            {getActionUsage()}
        };

        return actions;
    }
    
    /**
     * Return an array of association actions in the
     * pattern of which to build a popup toolbutton
     */
    private Object[] getAssociationActions() {
        // This calls the getters to fetch actions even though the
        // action variables are defined is instances of this class.
        // This is because any number of action getters could have
        // been overridden in a descendent and it is the action from
        // that overridden method that should be returned in the array.
        Object actions[][] = {
            {getActionAssociation(), getActionUniAssociation()},
            {getActionAggregation(), getActionUniAggregation()},
            {getActionComposition(), getActionUniComposition()}
        };

        return actions;
    }
    
    
    /**
     * Creates a new diagramname.
     * @return String
     */
    protected static String getNewDiagramName() {
        String name = null;
        name = "Class Diagram " + classDiagramSerial;
        classDiagramSerial++;
        if (!ProjectManager.getManager().getCurrentProject()
	        .isValidDiagramName(name)) {
            name = getNewDiagramName();
        }
        return name;
    }
    
    /**
     * @return Returns the actionAggregation.
     */
    protected Action getActionAggregation() {
        if (actionAggregation == null) {
            actionAggregation = makeCreateAssociationAction(
                ModelFacade.AGGREGATE_AGGREGATIONKIND,
                false,
                "Aggregation");
        }
        return actionAggregation;
    }
    /**
     * @return Returns the actionAssociation.
     */
    protected Action getActionAssociation() {
        if (actionAssociation == null) {
            actionAssociation = makeCreateAssociationAction(
                ModelFacade.NONE_AGGREGATIONKIND,
                false, "Association");
        }
        return actionAssociation;
    }
    /**
     * @return Returns the actionClass.
     */
    protected Action getActionClass() {
        if (actionClass == null) {
            actionClass = makeCreateNodeAction(ModelFacade.CLASS, "Class");
        }

        return actionClass;
    }
    /**
     * @return Returns the actionComposition.
     */
    protected Action getActionComposition() {
        if (actionComposition == null) {
            actionComposition = makeCreateAssociationAction(
                ModelFacade.COMPOSITE_AGGREGATIONKIND,
                false, "Composition");
        }
        return actionComposition;
    }
    /**
     * @return Returns the actionDepend.
     */
    protected Action getActionDependency() {
        if (actionDependency == null) {
            actionDependency =
                makeCreateEdgeAction(ModelFacade.DEPENDENCY, "Dependency");
        }
        return actionDependency;
    }
    
    /**
     * @return Returns the actionGeneralize.
     */
    protected Action getActionGeneralization() {
        if (actionGeneralization == null) {
            actionGeneralization = makeCreateEdgeAction(
                    ModelFacade.GENERALIZATION,
                    "Generalization");
        }
        
        return actionGeneralization;
    }

    /**
     * @return Returns the actionInterface.
     */
    protected Action getActionInterface() {
        if (actionInterface == null) {
            actionInterface =
                makeCreateNodeAction(ModelFacade.INTERFACE, "Interface");
        }
        return actionInterface;
    }
    
    /**
     * @return Returns the actionLink.
     */
    protected Action getActionLink() {
        if (actionLink == null) {
            actionLink = makeCreateEdgeAction(ModelFacade.LINK, "Link");
        }

        return actionLink;
    }
    /**
     * @return Returns the actionModel.
     */
    protected Action getActionModel() {
        if (actionModel == null) {
            actionModel = makeCreateNodeAction(ModelFacade.MODEL, "Model");
        }
        
        return actionModel;
    }
    /**
     * @return Returns the actionObject.
     */
    protected Action getActionObject() {
        if (actionObject == null) {
            actionObject =
                makeCreateNodeAction(ModelFacade.INSTANCE, "Instance");
        }

        return actionObject;
    }
    /**
     * @return Returns the actionPackage.
     */
    protected Action getActionPackage() {
        if (actionPackage == null) {
            actionPackage = 
                makeCreateNodeAction(ModelFacade.PACKAGE, "Package");
        }

        return actionPackage;
    }
    /**
     * @return Returns the actionPermission.
     */
    protected Action getActionPermission() {
        if (actionPermission == null) {
            actionPermission = 
                makeCreateEdgeAction(ModelFacade.PERMISSION, "Permission");
        }

        return actionPermission;
    }
    
    /**
     * @return Returns the actionRealize.
     */
    protected Action getActionRealization() {
        if (actionRealization == null) {
            actionRealization =
                makeCreateEdgeAction(ModelFacade.ABSTRACTION, "Realization");
        }

        return actionRealization;
    }
    
    /**
     * @return Returns the actionSubsystem.
     */
    protected Action getActionSubsystem() {
        if (actionSubsystem == null) {
            actionSubsystem = 
                makeCreateNodeAction(ModelFacade.SUBSYSTEM, "Subsystem");
        }
        return actionSubsystem;
    }
    
    /**
     * @return Returns the actionUniAggregation.
     */
    protected Action getActionUniAggregation() {
        if (actionUniAggregation == null) {
            actionUniAggregation = makeCreateAssociationAction(
                ModelFacade.AGGREGATE_AGGREGATIONKIND,
                true,
                "UniAggregation");
        }
        return actionUniAggregation;
    }
    
    /**
     * @return Returns the actionUniAssociation.
     */
    protected Action getActionUniAssociation() {
        if (actionUniAssociation == null) {
            actionUniAssociation = makeCreateAssociationAction(
                ModelFacade.NONE_AGGREGATIONKIND,
                true,
                "UniAssociation");
        }
        return actionUniAssociation;
    }
    
    /**
     * @return Returns the actionUniComposition.
     */
    protected Action getActionUniComposition() {
        if (actionUniComposition == null) {
            actionUniComposition = makeCreateAssociationAction(
                ModelFacade.COMPOSITE_AGGREGATIONKIND,
                true,
                "UniComposition");
        }
        return actionUniComposition;
    }
    
    /**
     * @return Returns the actionUsage.
     */
    protected Action getActionUsage() {
        if (actionUsage == null) {
            actionUsage = makeCreateEdgeAction(ModelFacade.USAGE, "Usage");
        }
        return actionUsage;
    }

    /**
     * @return Returns the actionAttribute.
     */
    private Action getActionAttribute() {
        if (actionAttribute == null) {
            actionAttribute = ActionAddAttribute.getSingleton();
        }
        return actionAttribute;
    }

    /**
     * @return Returns the actionOperation.
     */
    private Action getActionOperation() {
        if (actionOperation == null) {
            actionOperation = ActionAddOperation.getSingleton();
        }
        return actionOperation;
    }
} /* end class UMLClassDiagram */
