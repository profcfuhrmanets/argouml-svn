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

package org.argouml.cognitive.checklist.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.tigris.gef.presentation.Fig;

import org.apache.log4j.Logger;
import org.argouml.cognitive.checklist.CheckItem;
import org.argouml.cognitive.checklist.CheckManager;
import org.argouml.cognitive.checklist.Checklist;
import org.argouml.cognitive.checklist.ChecklistStatus;
import org.argouml.kernel.DelayedChangeNotify;
import org.argouml.kernel.DelayedVChangeListener;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlModelEventPump;
import org.argouml.i18n.Translator;
import org.argouml.ui.LookAndFeelMgr;
import org.argouml.ui.TabSpawnable;
import org.argouml.ui.targetmanager.TargetEvent;
import org.argouml.uml.ui.TabModelTarget;

import ru.novosoft.uml.MElementEvent;
import ru.novosoft.uml.MElementListener;

/** Tab to show the checklist for a certain element.
 */
public class TabChecklist extends TabSpawnable
    implements TabModelTarget, ActionListener, ListSelectionListener
{

    ////////////////////////////////////////////////////////////////
    // instance variables
    private Object target;
    private TableModelChecklist tableModel = null;
    private boolean shouldBeEnabled = false;
    private JTable table = new JTable(10, 2);

    /**
     * The constructor.
     * 
     */
    public TabChecklist() {
	super("tab.checklist");

	tableModel = new TableModelChecklist(this);
	table.setModel(tableModel);

	Font labelFont = LookAndFeelMgr.getInstance().getSmallFont();
	table.setFont(labelFont);

	//_table.setRowSelectionAllowed(false);
	table.setIntercellSpacing(new Dimension(0, 1));
	table.setShowVerticalLines(false);
	table.getSelectionModel().addListSelectionListener(this);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

	TableColumn checkCol = table.getColumnModel().getColumn(0);
	TableColumn descCol = table.getColumnModel().getColumn(1);
	checkCol.setMinWidth(20);
	checkCol.setMaxWidth(30);
	checkCol.setWidth(30);
	descCol.setPreferredWidth(900);
	//descCol.setWidth(900);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	table.sizeColumnsToFit(-1);

	JScrollPane sp = new JScrollPane(table);

	setLayout(new BorderLayout());
	add(new JLabel(Translator.localize("tab.checklist.warning")), 
	    BorderLayout.NORTH);
	add(sp, BorderLayout.CENTER);
    }
    
    
    /** Converts a selected element to a target that is appropriate for a 
     * checklist.<p>
     *
     * The argument can be either 
     * a Fig, if a Figure when something is selected from a diagram
     * or a model element when an object is selected from the explorer.<p>
     *
     * @param t that is an object.
     * @return target that is always model element.
     */
    private Object findTarget(Object t) {
        if (t instanceof Fig) {
            Fig f = (Fig) t;
            t = f.getOwner();
        }
        return t;
    }
 

    ////////////////////////////////////////////////////////////////
    // accessors
    /** Actually prepares the Tab.
     *
     * @param t is the target to show the list for.
     */
    public void setTarget(Object t) {
        target = findTarget(t);
        
        if (target == null) {
            shouldBeEnabled = false;
            return;
        }

	shouldBeEnabled = true;
	Checklist cl = CheckManager.getChecklistFor(target);
	if (cl == null) {
	    target = null;
	    shouldBeEnabled = false;
	    return;
	}

	tableModel.setTarget(target);

	TableColumn checkCol = table.getColumnModel().getColumn(0);
	TableColumn descCol = table.getColumnModel().getColumn(1);
	checkCol.setMinWidth(20);
	checkCol.setMaxWidth(30);
	checkCol.setWidth(30);
	//descCol.setWidth(900);
	descCol.setPreferredWidth(900);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	resizeColumns();
	validate();
    }
    
    /**
     * @see org.argouml.ui.TabTarget#getTarget()
     */
    public Object getTarget() { return target; }

    /**
     * @see org.argouml.ui.TabTarget#refresh()
     */
    public void refresh() { setTarget(target); }

    /** Decides if the tab should be enabled or not.<p>
     *
     * @param t is the object element that it is then enabled for
     * @return true if it should be enabled.
     */
    public boolean shouldBeEnabled(Object t) {
        t = findTarget(t);
  
        if (t == null) {
            shouldBeEnabled = false;
            return shouldBeEnabled;
        }
        
	shouldBeEnabled = true;
	Checklist cl = CheckManager.getChecklistFor(t);
	if (cl == null) {
	    shouldBeEnabled = false;
	    return shouldBeEnabled;
	}

	return shouldBeEnabled;
    }

    /**
     * Resize the columns to fit. 
     */
    public void resizeColumns() {
	table.sizeColumnsToFit(0);
    }

    ////////////////////////////////////////////////////////////////
    // event handling

    /**
     * Enable buttons when selection made.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent lse) {
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetAdded(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetAdded(TargetEvent e) {

    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetRemoved(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetRemoved(TargetEvent e) {
	setTarget(e.getNewTarget());
    }

    /**
     * @see org.argouml.ui.targetmanager.TargetListener#targetSet(org.argouml.ui.targetmanager.TargetEvent)
     */
    public void targetSet(TargetEvent e) {
	setTarget(e.getNewTarget());
    }

} /* end class TabChecklist */




class TableModelChecklist extends AbstractTableModel
    implements VetoableChangeListener, DelayedVChangeListener, MElementListener
{
    private static final Logger LOG =
        Logger.getLogger(TableModelChecklist.class);

    ////////////////
    // instance varables
    private Object target;
    private TabChecklist panel;

    ////////////////
    // constructor
    public TableModelChecklist(TabChecklist tc) { panel = tc; }

    ////////////////
    // accessors
    private UmlModelEventPump getPump() {
	return UmlModelEventPump.getPump();
    }

    public void setTarget(Object t) {
	if (ModelFacade.isAElement(target))
	    getPump().removeModelEventListener(this, target);
	target = t;
	if (ModelFacade.isAElement(target))
	    getPump().addModelEventListener(this, target);
	fireTableStructureChanged();
    }

    ////////////////
    // TableModel implemetation
    public int getColumnCount() { return 2; }

    public String  getColumnName(int c) {
	if (c == 0) return "X";
	if (c == 1) return "Description";
	return "XXX";
    }

    public Class getColumnClass(int c) {
	if (c == 0) return Boolean.class;
	if (c == 1) return String.class;
	return String.class;
    }

    public boolean isCellEditable(int row, int col) {
	return col == 0;
    }

    public int getRowCount() {
	if (target == null) return 0;
	Checklist cl = CheckManager.getChecklistFor(target);
	if (cl == null) return 0;
	return cl.size();
    }

    public Object getValueAt(int row, int col) {
	Checklist cl = CheckManager.getChecklistFor(target);
	if (cl == null) return "no checklist";
	CheckItem ci = cl.elementAt(row);
	if (col == 0) {
	    ChecklistStatus stat = CheckManager.getStatusFor(target);
	    return (stat.contains(ci)) ? Boolean.TRUE : Boolean.FALSE;
	}
	else if (col == 1) {
	    return ci.getDescription(target);
	}
	else
	    return "CL-" + row * 2 + col;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)  {
	LOG.debug("setting table value " + rowIndex + ", " + columnIndex);
	if (columnIndex != 0) return;
	if (!(aValue instanceof Boolean)) return;
	boolean val = ((Boolean) aValue).booleanValue();
	Checklist cl = CheckManager.getChecklistFor(target);
	if (cl == null) return;
	CheckItem ci = cl.elementAt(rowIndex);
	if (columnIndex == 0) {
	    ChecklistStatus stat = CheckManager.getStatusFor(target);
	    if (val) stat.addItem(ci);
	    else stat.removeItem(ci);
	}
    }

    ////////////////
    // event handlers
    public void propertySet(MElementEvent mee) {
    }
    public void listRoleItemSet(MElementEvent mee) {
    }
    public void recovered(MElementEvent mee) {
    }
    public void removed(MElementEvent mee) {
    }
    public void roleAdded(MElementEvent mee) {
    }
    public void roleRemoved(MElementEvent mee) {
    }


    public void vetoableChange(PropertyChangeEvent pce) {
	DelayedChangeNotify delayedNotify = new DelayedChangeNotify(this, pce);
	SwingUtilities.invokeLater(delayedNotify);
    }

    public void delayedVetoableChange(PropertyChangeEvent pce) {
	fireTableStructureChanged();
	panel.resizeColumns();
    }

} /* end class TableModelChecklist */
