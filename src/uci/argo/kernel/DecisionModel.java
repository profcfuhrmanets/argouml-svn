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

// File: DecisionModel.java
// Classes: DecisionModel
// Original Author: jrobbins@ics.uci.edu
// $Id$

package uci.argo.kernel;

import java.util.*;
import java.awt.*;
import uci.util.*;


/** The DecisionModel is part of the state of the Designer.  It
 *  describes what types of decisions, or design issues, the Designer
 *  is thinking about at the current time.  Critics that are relevant to
 *  those decisions are made active, Critics that are not relevant are
 *  made inactive.
 *
 *  Needs-More-Work: There is some notion that each decision has a
 *  certain importanance at a certain time, but I have not followed
 *  through on that because I don't have good examples of how to
 *  quantify the importance of a decision.
 *
 *  Needs-More-Work: Right now the individual decisions are just
 *  Strings, maybe they should have some non-atomic structure? */

public class DecisionModel extends Observable
implements java.io.Serializable {

  ////////////////////////////////////////////////////////////////
  // instance variables

  private Vector _decisions = new Vector();

  ////////////////////////////////////////////////////////////////
  // constructor

  public DecisionModel() { }

  ////////////////////////////////////////////////////////////////
  // accessors

  public Vector getDecisions() { return _decisions; }

  /** Reply true iff the Designer is considering the given decision. */
  public boolean isConsidering(String decision) {
    Decision d = findDecision(decision);
    if 	(null == d) return false;
    return d.getPriority() > 0;
  }

  public synchronized void setDecisionPriority(String decision, int priority) {
    Decision d = findDecision(decision);
    if 	(null == d) {
      d = new Decision(decision, priority);
      _decisions.addElement(d);
      return;
    }
    d.setPriority(priority);
    setChanged();
    notifyObservers(decision);
    //decision model listener
  }

  /** If the given decision is already defined, do nothing. If it is
   * not already defined, set it to the given initial priority. */
  public void defineDecision(String decision, int priority) {
    Decision d = findDecision(decision);
    if (d == null) setDecisionPriority(decision, priority);
  }

  /** The Designer has indicated that he is now interested in the
   * given decision. */
  public void startConsidering(String decision) {
    setDecisionPriority(decision, 1);
  }

  /** The Designer has indicated that he is not interested in the
   * given decision right now. */
  public void stopConsidering(String decision) {
    setDecisionPriority(decision, 0);
  }

  protected Decision findDecision(String decName) {
    Enumeration enum = _decisions.elements();
    while (enum.hasMoreElements()) {
      Decision d = (Decision) enum.nextElement();
      if (decName.equals(d.getName())) return d;
    }
    return null;
  }

} /* end class DecisionModel */
