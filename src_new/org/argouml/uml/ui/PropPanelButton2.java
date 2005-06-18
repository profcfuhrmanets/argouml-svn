// $Id$
// Copyright (c) 2002-2005 The Regents of the University of California. All
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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @since Oct 3, 2002
 * @author jaap.branderhorst@xs4all.nl
 */
public final class PropPanelButton2 extends JButton {

    /**
     * Constructor for PropPanelButton2.
     * @param a the action
     */
    public PropPanelButton2(Action a) {
        super(a);
        setText(""); // just the icon and possibly a tooltip
    }

    /**
     * The constructor.
     * @deprecated by Bob Tarling. The Action class should define its own Icon
     * then this constructor is no longer needed. Once this constructor
     * is no longer used then this whole class is no longer needed.
     * See issue 3260.
     *
     * @param a the action for this button
     * @param i the icon to be shown
     */
    public PropPanelButton2(Action a, Icon i) {
        this(a);
        a.putValue(Action.SMALL_ICON, i);
    }
}
