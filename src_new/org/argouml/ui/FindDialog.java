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

package org.argouml.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.argouml.i18n.Translator;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.ModelFacade;
import org.argouml.uml.PredicateFind;
import org.argouml.uml.cognitive.ChildGenFind;
import org.tigris.gef.util.Predicate;
import org.tigris.gef.util.PredicateStringMatch;
import org.tigris.gef.util.PredicateType;


/**
 * This is one of the few classes in Argo that is
 * self running (i.e. not modal).
 *
 * The search is buggy and needs work.
 */
public class FindDialog extends ArgoDialog
    implements ActionListener, MouseListener {

    ////////////////////////////////////////////////////////////////
    // class variables

    private static FindDialog instance;
    private static int nextResultNum = 1;

    private static int numFinds = 0;

    ////////////////////////////////////////////////////////////////
    // instance variables
    private JButton     search     = new JButton("Find");
    private JButton     clearTabs  = new JButton("Clear Tabs");
    private JTabbedPane tabs       = new JTabbedPane();
    private JPanel      nameLocTab = new JPanel();
    private JPanel     modifiedTab = new JPanel();
    private JPanel      tagValsTab = new JPanel();
    private JPanel  constraintsTab = new JPanel();

    private JComboBox   elementName = new JComboBox();
    private JComboBox   diagramName = new JComboBox();
    private JComboBox   location    = new JComboBox();
    private JComboBox   type        = new JComboBox();
    private JPanel      typeDetails = new JPanel();

    private JTabbedPane results     = new JTabbedPane();
    private JPanel      help        = new JPanel();
    private Vector      resultTabs  = new Vector();

    ////////////////////////////////////////////////////////////////
    // constructors

    /**
     * @return the instance of this dialog 
     */
    public static FindDialog getInstance() {
        if (instance == null) {
            instance = new FindDialog();
        }
        return instance;
    }
    
    /**
     * The constructor.
     * 
     */
    public FindDialog() {
        super(ProjectBrowser.getInstance(), "Find", 
                ArgoDialog.OK_CANCEL_OPTION, false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());

        initNameLocTab();
        tabs.addTab("Name and Location", nameLocTab);

        initModifiedTab();
        tabs.addTab("Last Modified", modifiedTab);
        tabs.setEnabledAt(1, false);

        initTagValsTab();
        tabs.addTab("Tagged Values", tagValsTab);
        tabs.setEnabledAt(2, false);

        initConstraintsTab();
        tabs.addTab(Translator.localize("tab.constraints"),
		     constraintsTab);
        tabs.setEnabledAt(3, false);

        //_tabs.addTab("Tagged Values", _tagValsTab);
        tabs.setMinimumSize(new Dimension(300, 250));

        JPanel north = new JPanel();
        north.setLayout(new BorderLayout());
        north.add(tabs, BorderLayout.CENTER);
        mainPanel.add(north, BorderLayout.NORTH);

        initHelpTab();
        results.addTab("Help", help);
        mainPanel.add(results, BorderLayout.CENTER);

        //     JPanel south = new JPanel();
        //     south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        //     JPanel buttonPane = new JPanel();
        //     buttonPane.setLayout(new GridLayout(1, 4));
        //     buttonPane.add(_clear);
        //     buttonPane.add(_spawn);
        //     buttonPane.add(_go);
        //     buttonPane.add(_close);
        //     south.add(buttonPane);
        //     getContentPane().add(south, BorderLayout.SOUTH);
        //     getRootPane().setDefaultButton(_search);
        search.addActionListener(this);
        results.addMouseListener(this);

        clearTabs.addActionListener(this);
        clearTabs.setEnabled(false);
        //     _spawn.addActionListener(this);
        //     _go.addActionListener(this);
        //     _close.addActionListener(this);
        //setSize(new Dimension(480, 550));
        
        setContent(mainPanel);
        
        getOkButton().setEnabled(false);
    }

    /**
     * Initialise the tab "Name and Location".
     */
    public void initNameLocTab() {
        elementName.setEditable(true);
        elementName.getEditor()
	    .getEditorComponent().setBackground(Color.white);
        diagramName.setEditable(true);
        diagramName.getEditor()
	    .getEditorComponent().setBackground(Color.white);

        elementName.addItem("*");
        diagramName.addItem("*");

        // TODO: add recent patterns
        GridBagLayout gb = new GridBagLayout();
        nameLocTab.setLayout(gb);

        JLabel elementNameLabel = new JLabel("Element Name:");
        JLabel diagramNameLabel = new JLabel("In Diagram:");
        JLabel typeLabel = new JLabel("Element Type:");
        JLabel locLabel = new JLabel("Find In:");

        location.addItem("Entire Project");
        /*      MVW: The following panel is not used at all. 
         *      So let's not show it. 
         *      See issue 2502. 
         */
        // _typeDetails.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        initTypes();

        typeDetails.setMinimumSize(new Dimension(200, 100));
        typeDetails.setPreferredSize(new Dimension(200, 100));
        typeDetails.setSize(new Dimension(200, 100));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = 3; c.ipady = 3;
        c.gridwidth = 1;

        c.gridx = 0;     c.gridy = 0;
        c.weightx = 0.0;
        gb.setConstraints(elementNameLabel, c);
        nameLocTab.add(elementNameLabel);

        c.gridx = 1;     c.gridy = 0;
        c.weightx = 1.0;
        gb.setConstraints(elementName, c);
        nameLocTab.add(elementName);

        c.gridx = 0;     c.gridy = 1;
        c.weightx = 0.0;
        gb.setConstraints(diagramNameLabel, c);
        nameLocTab.add(diagramNameLabel);

        c.gridx = 1;     c.gridy = 1;
        c.weightx = 1.0;
        gb.setConstraints(diagramName, c);
        nameLocTab.add(diagramName);

        // open space at gridy = 2

        c.gridx = 0;     c.gridy = 3;
        c.weightx = 0.0;
        gb.setConstraints(locLabel, c);
        nameLocTab.add(locLabel);

        c.gridx = 1;     c.gridy = 3;
        c.weightx = 1.0;
        gb.setConstraints(location, c);
        nameLocTab.add(location);

        SpacerPanel spacer = new SpacerPanel();
        c.gridx = 2;     c.gridy = 0;
        c.weightx = 0.0;
        gb.setConstraints(spacer, c);
        nameLocTab.add(spacer);

        c.gridx = 3;     c.gridy = 0;
        c.weightx = 0.0;
        gb.setConstraints(typeLabel, c);
        nameLocTab.add(typeLabel);

        c.gridx = 4;     c.gridy = 0;
        c.weightx = 1.0;
        gb.setConstraints(type, c);
        nameLocTab.add(type);

        c.gridx = 3;     c.gridy = 1;
        c.gridwidth = 2; c.gridheight = 5;
        gb.setConstraints(typeDetails, c);
        nameLocTab.add(typeDetails);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(1, 2, 5, 5));
        searchPanel.add(clearTabs);
        searchPanel.add(search);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        c.gridx = 0;     c.gridy = 4;
        c.weightx = 0.0; c.weighty = 0.0;
        c.gridwidth = 2; c.gridheight = 1;
        gb.setConstraints(searchPanel, c);
        nameLocTab.add(searchPanel);
    }

    /**
     * Initialise the help tab.
     */
    public void initHelpTab() {
        help.setLayout(new BorderLayout());
        JTextArea helpText = new JTextArea();
        String s; // TODO: i18n
        s = "Please follow these steps to find model elements:\n\n" 
            + "1. Enter search information in the tabs " 
                    + "at the top of this window.\n\n" 
            + "2. Press the \"Find\" button.  This will produce a new tab.\n\n" 
            + "3. The top half of each result tab lists each results.\n" 
            + "   + Single clicking on a result shows more " 
                    + "information about it,\n" 
            + "     including a list of related objects.\n" 
            + "   + Double clicking on a result jumps to the " 
                    + "selected diagram.\n\n"; 
//            + "You can \"tear-off\" a results tab by double clicking " 
//                    + "on the tab name.\n" 
//            + "If you accumulate too many tabs, press \"Clear " 
//                    + "Tabs\" to remove " 
//            + "them all.";
        helpText.setText(s);
        helpText.setEditable(false);
        help.add(new JScrollPane(helpText), BorderLayout.CENTER);
    }

    /**
     * Init the tab with the tagged values.
     * TODO: This tab does not work currently.
     */
    public void initTagValsTab() {
        //  _tag         = new JTextField();
        //  _val         = new JTextField();
    }

    /**
     * Init the Last Modified tab.
     * TODO: This tab does not work currently.
     */
    public void initModifiedTab() { }
    
    /**
     * Init the Constraints tab.
     * TODO: This tab does not work currently.
     */
    public void initConstraintsTab() { }


    /**
     * Init the modelelement types that we can look for.
     */
    public void initTypes() {
        type.addItem(PredicateMType.create());

        type.addItem(PredicateMType.create(ModelFacade.CLASS));
        type.addItem(PredicateMType.create(ModelFacade.INTERFACE));
        type.addItem(PredicateMType.create(ModelFacade.ACTOR));
        type.addItem(PredicateMType.create(ModelFacade.ASSOCIATION));
        type.addItem(PredicateMType.create(ModelFacade.ATTRIBUTE));
        type.addItem(PredicateMType.create(ModelFacade.CLASSIFIER));
        type.addItem(PredicateMType.create(ModelFacade.COMPOSITESTATE));
        type.addItem(PredicateMType.create(ModelFacade.DEPENDENCY));
        type.addItem(PredicateMType.create(ModelFacade.GENERALIZATION));
        type.addItem(PredicateMType.create(ModelFacade.INSTANCE));
        type.addItem(PredicateMType.create(ModelFacade.INTERFACE));
        type.addItem(PredicateMType.create(ModelFacade.LINK));
        type.addItem(PredicateMType.create(ModelFacade.CLASS));
        type.addItem(PredicateMType.create(ModelFacade.PACKAGE));
        type.addItem(PredicateMType.create(ModelFacade.OPERATION));
        type.addItem(PredicateMType.create(ModelFacade.PSEUDOSTATE));
        type.addItem(PredicateMType.create(ModelFacade.STATE));
        type.addItem(PredicateMType.create(ModelFacade.STATEVERTEX));
        type.addItem(PredicateMType.create(ModelFacade.TRANSITION));
        type.addItem(PredicateMType.create(ModelFacade.USE_CASE));

    }

    /**
     * @see org.argouml.swingext.Dialog#nameButtons()
     */
    protected void nameButtons() {
        super.nameButtons();
        nameButton(getOkButton(), "button.go-to-selection");
        nameButton(getCancelButton(), "button.close");
    }
    
    ////////////////////////////////////////////////////////////////
    // event handlers
    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     * java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == search) {
            doSearch();
        } 
        else if (e.getSource() == clearTabs) {
            doClearTabs();
        } 
        else if (e.getSource() == getOkButton()) {
            doGoToSelection();
        }
        else {
            super.actionPerformed(e);
        }
        //     if (e.getSource() == _spawn) doSpawn();
        //     if (e.getSource() == _go) doGo();
        //     if (e.getSource() == _close) doClose();
    }

    ////////////////////////////////////////////////////////////////
    // actions

    /**
     * Do the search.
     */
    public void doSearch() {
        numFinds++;
        String eName = "";
        if (elementName.getSelectedItem() != null) {
            eName += elementName.getSelectedItem();
            elementName.removeItem(eName);
            elementName.insertItemAt(eName, 0);
            elementName.setSelectedItem(eName);
        }
        String dName = "";
        if (diagramName.getSelectedItem() != null) {
            dName += diagramName.getSelectedItem();
            diagramName.removeItem(dName);
            diagramName.insertItemAt(dName, 0);
            diagramName.setSelectedItem(dName);
        }
        String name = eName;
        if (dName.length() > 0) name += " in " + dName;
        String typeName = type.getSelectedItem().toString();
        if (!typeName.equals("Any Type")) name += " " + typeName;
        if (name.length() == 0)
            name = "Find" + (nextResultNum++);
        if (name.length() > 15)
            name = name.substring(0, 12) + "...";

        String pName = "";

        Predicate eNamePred = PredicateStringMatch.create(eName);
        Predicate pNamePred = PredicateStringMatch.create(pName);
        Predicate dNamePred = PredicateStringMatch.create(dName);
        Predicate typePred = (Predicate) type.getSelectedItem();
        PredicateFind pred =
            new PredicateFind(eNamePred, pNamePred, dNamePred, typePred);

        ChildGenFind gen = ChildGenFind.getSingleton();
        Object root = ProjectManager.getManager().getCurrentProject();

        TabResults newResults = new TabResults();
        newResults.setTitle(name);
        newResults.setPredicate(pred);
        newResults.setRoot(root);
        newResults.setGenerator(gen);
        resultTabs.addElement(newResults);
        results.addTab(name, newResults);
        clearTabs.setEnabled(true);
        getOkButton().setEnabled(true);
        results.setSelectedComponent(newResults);
        location.addItem("In Tab: " + name);
        invalidate();
        results.invalidate();
        validate();
        newResults.run();
        newResults.requestFocus();
        newResults.selectResult(0);
    }

    /**
     * Clear the tabs.
     */
    public void doClearTabs() {
        int numTabs = resultTabs.size();
        for (int i = 0; i < numTabs; i++)
            results.remove((Component) resultTabs.elementAt(i));
        resultTabs.removeAllElements();
        clearTabs.setEnabled(false);
        getOkButton().setEnabled(false);
        doResetFields(false);
    }


    
    /**
     * Reset the fields.
     * 
     * @param complete if true, reset all 3 fields, otherwise only the latter 
     */
    private void doResetFields(boolean complete) {
        if (complete) {
            elementName.removeAllItems();
            diagramName.removeAllItems();
            elementName.addItem("*");
            diagramName.addItem("*");
        }
        location.removeAllItems();
        location.addItem("Entire Project");
    }

    /**
     * Reset all 3 fields.
     */
    public void doResetFields() {
        doResetFields(true);
    }
    
    /**
     * Execute the GoTo selection command.
     */
    public void doGoToSelection() {
        if (results.getSelectedComponent() instanceof TabResults) {
            ((TabResults) results.getSelectedComponent()).doDoubleClick();
        }
    }
  
    //   public void doSpawn() { }

    //   public void doGo() { }

    //   public void doClose() { }

    ////////////////////////////////////////////////////////////////
    // MouseListener implementation

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent me) { }
    
    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent me) { }
    
    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent me) { }
    
    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent me) { }
    
    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent me) {
        int tab = results.getSelectedIndex();
        if (tab != -1) {
            Rectangle tabBounds = results.getBoundsAt(tab);
            if (!tabBounds.contains(me.getX(), me.getY())) return;
            if (tab >= 1 && me.getClickCount() >= 2)
                myDoubleClick(tab - 1); //help tab is 0
        }
    }

    /**
     * React on a double-click on a given tab.
     * 
     * TODO: Since spawning is disabled, we cannot do this....
     * MVW: I commented out the code below since it leads to exceptions.
     * 
     * @param tab the given tab
     */
    public void myDoubleClick(int tab) {
//        JPanel t = (JPanel) resultTabs.elementAt(tab);
//        if (t instanceof TabSpawnable) {
//            ((TabSpawnable) t).spawn();
//            resultTabs.removeElementAt(tab);
//            location.removeItem("In Tab:" + ((TabSpawnable) t).getTitle());
//        }
    }

} /* end class FindDialog */


/** PredicateMType is a small helper class which removes a trailing
 *  M from the string representation of the Type, as all the types
 *  are MThings. Thus they are more human readable when displayed
 *  in the Find dialog
 */
class PredicateMType extends PredicateType
{
    protected PredicateMType(Class pats[]) {
        super(pats, pats.length);
    }
  
    protected PredicateMType(Class pats[], int numPats) {
        super(pats, numPats);
    }

    public static PredicateType create() {
        return new PredicateMType(null, 0);
    }

    public static PredicateType create(Object c0) {
        Class classes[] = new Class[1];
        classes[0] = (Class) c0;
        return new PredicateMType(classes);
    }

    public static PredicateType create(Object c0, Object c1) {
        Class classes[] = new Class[2];
        classes[0] = (Class) c0;
        classes[1] = (Class) c1;
        return new PredicateMType(classes);
    }

    public static PredicateType create(Object c0, Object c1, Object c2) {
        Class classes[] = new Class[3];
        classes[0] = (Class) c0;
        classes[1] = (Class) c1;
        classes[2] = (Class) c2;
        return new PredicateMType(classes);
    }


    public String toString() {
        String result = super.toString();
        if (result.startsWith("M")) result = result.substring(1);
        return result;
    }
}
