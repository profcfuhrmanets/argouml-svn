// Copyright (c) 1995, 1996 Regents of the University of California.
// All rights reserved.
//
// This software was developed by the Arcadia project
// at the University of California, Irvine.
//
// Redistribution and use in source and binary forms are permitted
// provided that the above copyright notice and this paragraph are
// duplicated in all such forms and that any documentation,
// advertising materials, and other materials related to such
// distribution and use acknowledge that the software was developed
// by the University of California, Irvine.  The name of the
// University may not be used to endorse or promote products derived
// from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
// WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.

// File: ActionSelectInvert.java
// Classes: ActionSelectInvert
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.gef;

import java.awt.*;
import java.util.*;

/** Action to select all the Figs in the editor's current
 *  view that were not previously selected.
 *  <A HREF="../features.html#select_inversion">
 *  <TT>FEATURE: select_inversion</TT></A>
 *
 * @see ModeSelect#bindKeys */

public class ActionSelectInvert extends Action {

  public ActionSelectInvert() { }

  public String name() { return "Select all unselected Figs"; }

  public void doIt(java.awt.Event e) {
    Editor ce = Globals.curEditor();
    Vector selected = ce.selectedFigs();
    Vector diagramContents = ce.getLayerManager().contents();
    Vector inverse = new Vector(diagramContents.size());
    Enumeration contEnum = diagramContents.elements();
    while (contEnum.hasMoreElements()) {
      Object dc = contEnum.nextElement();
      if (!selected.contains(dc)) inverse.addElement(dc);
    }
    ce.select(inverse);
  }

  public void undoIt() {
    System.out.println("Undo does not make sense for ActionSelectInvert");
  }

} /* end class ActionSelectInvert */

