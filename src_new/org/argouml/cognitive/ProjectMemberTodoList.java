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

package org.argouml.cognitive;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;

import org.xml.sax.SAXException;

import org.argouml.kernel.Project;
import org.argouml.kernel.ProjectMember;
import org.argouml.ocl.OCLExpander;
import org.argouml.xml.todo.ResolvedCriticXMLHelper;
import org.argouml.xml.todo.ToDoItemXMLHelper;
import org.argouml.xml.todo.TodoParser;
import org.tigris.gef.ocl.TemplateReader;


/**
 * Helper class to act as a project member on behalf of the todo list.
 * It helps the todo list get loaded and saved together with the rest
 * of the project.
 *
 * @author	Michael Stockman
 */
public class ProjectMemberTodoList extends ProjectMember {
    
    private static final Logger LOG =
        Logger.getLogger(ProjectMemberTodoList.class);

    private static final String TO_DO_TEE = "/org/argouml/xml/dtd/todo.tee";
    private static final String TO_DO_EXT = ".todo";

    private OCLExpander expander = null;

    /**
     * The constructor. 
     * 
     * @param name the name
     * @param p the project
     */
    public ProjectMemberTodoList(String name, Project p) {
    	super(name, p);
    }

    /**
     * @see org.argouml.kernel.ProjectMember#getType()
     */
    public String getType() {
        return "todo";
    }

    /**
     * @see org.argouml.kernel.ProjectMember#getFileExtension()
     */
    public String getFileExtension() {
        return TO_DO_EXT;
    }

    /**
     * @return a vector containing the to do list
     */
    public Vector getToDoList() {
        Vector in, out;
        ToDoItem tdi;
        Designer dsgr;
        int i;
        
        dsgr = Designer.theDesigner();
        in = dsgr.getToDoList().getToDoItems();
        out = new Vector();
        for (i = 0; i < in.size(); i++) {
            try {
            	tdi = (ToDoItem) in.elementAt(i);
            	if (tdi == null) {
                    continue;
                }
            } catch (ClassCastException e) {
                continue;
            }
        
            if (tdi.getPoster() instanceof Designer) {
                out.addElement(new ToDoItemXMLHelper(tdi));
            }
        }
        return out;
    }

    /**
     * @return Vector conaining the resolved critics list
     */
    public Vector getResolvedCriticsList() {
    	Vector in, out;
    	ResolvedCritic rci;
    	Designer dsgr;
    	int i;
    
    	dsgr = Designer.theDesigner();
    	in = dsgr.getToDoList().getResolvedItems();
    	out = new Vector();
    	for (i = 0; i < in.size(); i++) {
    	    try {
        	rci = (ResolvedCritic) in.elementAt(i);
        	if (rci == null) {
        		    continue;
                }
    	    }
    	    catch (ClassCastException e) {
        		continue;
    	    }
    	    out.addElement(new ResolvedCriticXMLHelper(rci));
    	}
    	return out;
    }

    /**
     * @param is an InputStream
     */
    public void load(InputStream is) {
        TodoParser.SINGLETON.readTodoList(is, true);
    }

    /**
     * @see org.argouml.kernel.ProjectMember#load()
     */
    public void load() throws IOException, SAXException {
    	InputStream is = null;
    	if (getURL() != null) {
    	    is = getURL().openStream();
    	    load(is);
    	}
    }

    /**
     * @see org.argouml.kernel.ProjectMember#save(java.io.Writer)
     */
    public void save(Writer writer) {
        if (writer == null) {
            LOG.warn("ProjectMemberTodoList.cognitive.argouml.org:"
        	     + " No writer specified");
            return;
        }
        
        if (expander == null) {
            Hashtable templates = TemplateReader.readFile(TO_DO_TEE);
            expander = new OCLExpander(templates);
        }
        
        expander.expand(writer, this, "", "");
        
        LOG.debug("Done saving TO DO LIST!!!");
    }
}

