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


package org.argouml.language.java.generator;

import org.argouml.model.Model;

import junit.framework.TestCase;

/**
 * @author MarkusK
 */
public class TestGeneratorJava extends TestCase {

    Object namespace;

    Object class1;

    Object inter1;

    /**
     * Constructor for TestGeneratorJava.
     * 
     * @param arg0
     */
    public TestGeneratorJava(String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Object mmodel = Model.getModelManagementFactory().createModel();
        Model.getCoreHelper().setName(mmodel, "untitledModel");
        Model.getModelManagementFactory().setRootModel(mmodel);
        namespace = Model.getModelManagementFactory().createPackage();
        class1 = Model.getCoreFactory().buildClass("Class1", namespace);
        inter1 = Model.getCoreFactory().buildInterface("Inter1", namespace);

    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * check the Java Code Generator does not generate a protected class ....
     */
    public void testGenerateClassifierStart() {
        StringBuffer result;

        Model.getCoreHelper().setVisibility(class1,
                Model.getVisibilityKind().getPublic());
        result = GeneratorJava.getInstance().generateClassifierStart(class1);
        assertTrue("A class should have public in its specification", result
                .indexOf("public") == 0);

        Model.getCoreHelper().setVisibility(class1,
                Model.getVisibilityKind().getProtected());
        result = GeneratorJava.getInstance().generateClassifierStart(class1);
        assertTrue("A class should not have protected in its specification",
                result.indexOf("protected") == -1);
    }

}
