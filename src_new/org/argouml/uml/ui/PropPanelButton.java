// $Id$
// Copyright (c) 1996-2001 The Regents of the University of California. All
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

package org.argouml.uml.ui;

import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import ru.novosoft.uml.MElementEvent;

/**
 *   This abstract class provides the basic layout and event dispatching
 *   support for all Property Panels.  The property panel is layed out
 *   as a number (specified in the constructor) of equally sized panels
 *   that split the available space.  Each panel has a column of
 *   "captions" and matching column of "fields" which are laid out
 *   indepently from the other panels.
 */

public class PropPanelButton extends JButton 
    implements ActionListener, UMLUserInterfaceComponent {
    
    private static final Logger LOG = 
        Logger.getLogger(PropPanelButton.class);

    private PropPanel propPanel;

    /**
     * The action method to be executed.
     */
    private Method actionMethod;
    
    /**
     * The action to be exectuted.  Either action or action method 
     * must be set but not both.
     */
    private Action action;
    
    private Method enabledMethod;
    private static Object[] noArgs = {};

    /** 
     * Constructor for a new button in the propertypanel.
     * 
     * @param thePropPanel the property panel to use (usually this)
     * @param icon the icon of this button (choose one 
     *             from PropPanelModelElement)
     * @param toolTipText the text for the tooltip
     * @param theAction the action to be executed. Must be null 
     *                  if actionMethod is filled.
     */
    public PropPanelButton(
            PropPanel thePropPanel,
            Icon icon,
            String toolTipText,
            Action theAction) {

        super(icon);

        propPanel = thePropPanel;

        setToolTipText(toolTipText);

        Class propPanelClass = thePropPanel.getClass();
        Class[] noClass = {};
        
        if (theAction != null) {
            action = theAction;
        } else
            throw new IllegalArgumentException("Action must be indicated.");
        
        setEnabled(false);

        thePropPanel.addButton(this);
        addActionListener(this);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetReasserted()
     */
    public void targetReasserted() {
        boolean enabled = false;
        Object target = propPanel.getTarget();
        if (target != null && (actionMethod != null || action != null) 
                && propPanel != null) {
            enabled = true;
            if (enabledMethod != null) {
                try {
                    enabled = ((Boolean) 
                        enabledMethod.invoke(propPanel, noArgs)).booleanValue();
                } catch (InvocationTargetException ex) {
                    LOG.error(ex.getTargetException().toString() 
                        + " is InvocationTargetException in PropPanelButton", 
                        ex.getTargetException());
                    LOG.error("Container: " + propPanel.getClass().getName());
                    LOG.error("ActionMethod: " + actionMethod.toString());
                } catch (Exception e) {
                    LOG.error(e.toString() + " in PropPanelButton", e);
                }
            } else 
                if (action != null) {
                    enabled = action.isEnabled();
                } 
            
        }

        setEnabled(enabled);
    }

    /**
     * @see org.argouml.uml.ui.UMLUserInterfaceComponent#targetChanged()
     */
    public void targetChanged() {
        targetReasserted();
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleAdded(ru.novosoft.uml.MElementEvent)
     */
    public void roleAdded(final MElementEvent p1) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#recovered(ru.novosoft.uml.MElementEvent)
     */
    public void recovered(final MElementEvent p1) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#roleRemoved(ru.novosoft.uml.MElementEvent)
     */
    public void roleRemoved(final MElementEvent p1) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#listRoleItemSet(ru.novosoft.uml.MElementEvent)
     */
    public void listRoleItemSet(final MElementEvent p1) {

    }

    /**
     * @see ru.novosoft.uml.MElementListener#removed(ru.novosoft.uml.MElementEvent)
     */
    public void removed(final MElementEvent p1) {
    }

    /**
     * @see ru.novosoft.uml.MElementListener#propertySet(ru.novosoft.uml.MElementEvent)
     */
    public void propertySet(final MElementEvent event) {
    }

    /** 
     * ActionPerfomed invokes the defined action-method via reflection for this
     * button (deprecated WOW), or the actionPerformed of the given Action. 
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final java.awt.event.ActionEvent event) {

        if (actionMethod != null && propPanel != null) {
            try {
                actionMethod.invoke(propPanel, noArgs);
            } catch (InvocationTargetException ex) {
                LOG.error(ex.getTargetException().toString() 
                        + " is InvocationTargetException in PropPanelButton", 
                        ex.getTargetException());
                LOG.error("Container: " + propPanel.getClass().getName());
                LOG.error("ActionMethod: " + actionMethod.toString());
	    } catch (Exception e) {
                LOG.error(e.toString() + " in PropPanelButton.actionPerformed", 
                        e);
                LOG.error("Container: " + propPanel.getClass().getName());
                LOG.error("ActionMethod: " + actionMethod.toString());
            }
        } else
            if (action != null) {
                action.actionPerformed(event);
            } else
                throw new IllegalStateException(
                        "No action or action method indicated");
    }
}
