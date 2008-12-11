// $Id$
// Copyright (c) 2005-2008 The Regents of the University of California. All
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

package org.argouml.persistence;

import java.awt.Rectangle;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.argouml.uml.diagram.ArgoDiagram;
import org.argouml.uml.diagram.AttributesCompartmentContainer;
import org.argouml.uml.diagram.DiagramSettings;
import org.argouml.uml.diagram.ExtensionsCompartmentContainer;
import org.argouml.uml.diagram.OperationsCompartmentContainer;
import org.argouml.uml.diagram.PathContainer;
import org.argouml.uml.diagram.StereotypeContainer;
import org.argouml.uml.diagram.VisibilityContainer;
import org.argouml.uml.diagram.ui.FigEdgeModelElement;
import org.argouml.uml.diagram.ui.FigEdgePort;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.persistence.pgml.Container;
import org.tigris.gef.persistence.pgml.FigEdgeHandler;
import org.tigris.gef.persistence.pgml.FigGroupHandler;
import org.tigris.gef.persistence.pgml.HandlerStack;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigGroup;
import org.tigris.gef.presentation.FigNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// TODO: Move to Diagram subsystem?

/**
 * The PGML Parser. <p>
 * 
 * This replaces much of the identically named class from GEF.
 */
class PGMLStackParser
    extends org.tigris.gef.persistence.pgml.PGMLStackParser {

    /**
     * Logger.
     */
    private static final Logger LOG =
        Logger.getLogger(PGMLStackParser.class);

    private List<EdgeData> figEdges = new ArrayList<EdgeData>(50);
    
    private LinkedHashMap<FigEdge, Object> modelElementsByFigEdge =
        new LinkedHashMap<FigEdge, Object>(50);

    private DiagramSettings diagramSettings;
    
    /**
     * Constructor.
     * @param modelElementsByUuid a map of model elements indexed
     *                            by a unique string identifier.
     * @deprecated for 0.27.2 by tfmorris.  Use 
     * {@link #PGMLStackParser(Map, DiagramSettings)}/
     */
    @Deprecated
    public PGMLStackParser(Map modelElementsByUuid) {
        super(modelElementsByUuid);
        // TODO: Use stylesheet to convert or wait till we use Fig
        // factories in diagram subsystem.
        // What is the last version that used FigNote?
        addTranslation("org.argouml.uml.diagram.ui.FigNote",
        	"org.argouml.uml.diagram.static_structure.ui.FigComment");
        addTranslation("org.argouml.uml.diagram.static_structure.ui.FigNote",
            "org.argouml.uml.diagram.static_structure.ui.FigComment");
        addTranslation("org.argouml.uml.diagram.state.ui.FigState",
            "org.argouml.uml.diagram.state.ui.FigSimpleState");
        addTranslation("org.argouml.uml.diagram.ui.FigCommentPort",
            "org.argouml.uml.diagram.ui.FigEdgePort");
        addTranslation("org.tigris.gef.presentation.FigText",
                "org.argouml.uml.diagram.ui.ArgoFigText");
        addTranslation("org.tigris.gef.presentation.FigLine",
                "org.argouml.gefext.ArgoFigLine");
        addTranslation("org.tigris.gef.presentation.FigPoly",
                "org.argouml.gefext.ArgoFigPoly");
        addTranslation("org.tigris.gef.presentation.FigCircle",
                "org.argouml.gefext.ArgoFigCircle");
        addTranslation("org.tigris.gef.presentation.FigRect",
                "org.argouml.gefext.ArgoFigRect");
        addTranslation("org.tigris.gef.presentation.FigRRect",
                "org.argouml.gefext.ArgoFigRRect");
        addTranslation(
                "org.argouml.uml.diagram.deployment.ui.FigMNodeInstance",
                "org.argouml.uml.diagram.deployment.ui.FigNodeInstance");
        addTranslation("org.argouml.uml.diagram.ui.FigRealization",
                "org.argouml.uml.diagram.ui.FigAbstraction");
        // Replace any old sequence figs with Seq2
        // this may change if we refactor package structure
        addTranslation("org.argouml.uml.diagram.sequence.ui.UMLSequenceDiagram",
                "org.argouml.uml.diagram.sequence2.ui.UMLSequenceDiagram");
        addTranslation("org.argouml.uml.diagram.sequence.ui.FigCreateActionMessage",
                "org.argouml.uml.diagram.sequence2.ui.FigMessage");
        addTranslation("org.argouml.uml.diagram.sequence.ui.FigDeleteActionMessage",
                "org.argouml.uml.diagram.sequence2.ui.FigMessage");
        addTranslation("org.argouml.uml.diagram.sequence.ui.FigCallActionMessage",
                "org.argouml.uml.diagram.sequence2.ui.FigMessage");
        addTranslation("org.argouml.uml.diagram.sequence.ui.FigReturnActionMessage",
                "org.argouml.uml.diagram.sequence2.ui.FigMessage");
        addTranslation("org.argouml.uml.diagram.sequence.ui.FigClassifierRole",
                "org.argouml.uml.diagram.sequence2.ui.FigClassifierRole");
    }
    
    /**
     * Construct a PGML parser with the given HREF/Object map and default
     * diagram settings.
     * 
     * @param modelElementsByUuid map of HREF ids to objects used to associate
     *            Figs with their owning model elements
     * @param defaultSettings default diagram settings to use for newly created
     *            diagram and its contained Figs
     */
    public PGMLStackParser(Map<String, Object> modelElementsByUuid, 
            DiagramSettings defaultSettings) {
        // TODO: Move addTranslation here when deprecated constructor is removed
        this(modelElementsByUuid);
        diagramSettings = defaultSettings;
    }

    /*
     * @see org.tigris.gef.persistence.pgml.HandlerFactory#getHandler(
     *         HandlerStack, Object, String, String, String, Attributes)
     */
    public DefaultHandler getHandler(HandlerStack stack,
                                             Object container,
                                             String uri,
                                             String localname,
                                             String qname,
                                             Attributes attributes)
        throws SAXException {

        String href = attributes.getValue("href");
        Object owner = null;
        
        if (href != null) {
            owner = findOwner(href);
            if (owner == null) {
                LOG.warn("Found href of " 
                	+ href
                	+ " with no matching element in model");
                return null;
            }
        }
	
        // Ignore non-private elements within FigNode groups
        if (container instanceof FigGroupHandler) {
            FigGroup group = ((FigGroupHandler) container).getFigGroup();
            if (group instanceof FigNode && !qname.equals("private")) {
                return null;
            }
        }

        // Handle ItemUID in container contents
        if (qname.equals("private") && (container instanceof Container)) {
            return new PrivateHandler(this, (Container) container);
        }
        
        DefaultHandler handler =
            super.getHandler(stack, container, uri, localname, qname,
                    attributes);

        if (handler instanceof FigEdgeHandler) {
            return new org.argouml.persistence.FigEdgeHandler(
                    this, ((FigEdgeHandler) handler).getFigEdge());
        }

        return handler;

    }

    /*
     * @see org.tigris.gef.persistence.pgml.PGMLStackParser#setAttrs(
     *         org.tigris.gef.presentation.Fig, org.xml.sax.Attributes)
     */
    protected final void setAttrs(Fig f, Attributes attrList)
        throws SAXException {
        
        if (f instanceof FigGroup) {
            FigGroup group = (FigGroup) f;
            String clsNameBounds = attrList.getValue("description");
            if (clsNameBounds != null) {
                StringTokenizer st =
                    new StringTokenizer(clsNameBounds, ",;[] ");
                // Discard class name, x y w h
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }
                if (st.hasMoreElements()) {
                    st.nextToken();
                }

                Map<String, String> attributeMap = interpretStyle(st);
                setStyleAttributes(group, attributeMap);
            }
        }

        // TODO: Attempt to move the following code to GEF

        String name = attrList.getValue("name");
        if (name != null && !name.equals("")) {
            registerFig(f, name);
        }

        setCommonAttrs(f, attrList);

        final String href = attrList.getValue("href");
        if (href != null && !href.equals("")) {
            Object modelElement = findOwner(href);
            if (modelElement == null) {
                LOG.error("Can't find href of " + href);
                throw new SAXException("Found href of " + href
				       + " with no matching element in model");
            }
            if (f.getOwner() != modelElement) {
                // Assign nodes immediately but edges later. See issue 4310.
                if (f instanceof FigEdge) {
                    modelElementsByFigEdge.put((FigEdge) f, modelElement);
                } else {
                    f.setOwner(modelElement);
                }
            } else {
                LOG.debug("Ignoring href on " + f.getClass().getName()
			 + " as it's already set");
            }
        }
    }

    /**
     * The StringTokenizer is expected to be positioned at the start of a string
     * of style identifiers in the format name=value;name=value;name=value....
     * Each name value pair is interpreted and the Fig configured accordingly.
     * The value is optional and will default to a value applicable for its
     * name.
     * The current applicable names are operationsVisible and attributesVisible
     * and are used to show or hide the compartments within Class and Interface.
     * The default values are true.
     * @param st The StrinkTokenizer positioned at the first style identifier
     * @return a map of attributes
     */
    private Map<String, String> interpretStyle(StringTokenizer st) {
        Map<String, String> map = new HashMap<String, String>();
        String name;
        String value;
        
        while (st.hasMoreElements()) {
            String namevaluepair = st.nextToken();
            int equalsPos = namevaluepair.indexOf('=');
            if (equalsPos < 0) {
                name = namevaluepair;
                value = "true";
            } else {
                name = namevaluepair.substring(0, equalsPos);
                value = namevaluepair.substring(equalsPos + 1);
            }

            map.put(name, value);
        }
        return map;
    }

    /**
     * Set the fig style attributes. This should move into
     * the render factories as described in issue 859.
     * @param fig the fig to style.
     * @param attributeMap a map of name value pairs
     */
    private void setStyleAttributes(Fig fig, Map<String, String> attributeMap) {
        
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            final String name = entry.getKey();
            final String value = entry.getValue();

            if ("operationsVisible".equals(name)) {
                ((OperationsCompartmentContainer) fig)
                    .setOperationsVisible(value.equalsIgnoreCase("true"));
            } else if ("attributesVisible".equals(name)) {
                ((AttributesCompartmentContainer) fig)
                    .setAttributesVisible(value.equalsIgnoreCase("true"));
            } else if ("stereotypeVisible".equals(name)) {
                ((StereotypeContainer) fig)
                    .setStereotypeVisible(value.equalsIgnoreCase("true"));
            } else if ("visibilityVisible".equals(name)) {
                ((VisibilityContainer) fig)
                .setVisibilityVisible(value.equalsIgnoreCase("true"));
            } else if ("pathVisible".equals(name)) {
                ((PathContainer) fig)
                    .setPathVisible(value.equalsIgnoreCase("true"));
            } else if ("extensionPointVisible".equals(name)) {
                ((ExtensionsCompartmentContainer) fig)
                    .setExtensionPointVisible(value.equalsIgnoreCase("true"));
            }
        }
    }

    /**
     * Read and parse the input stream to create a new diagram and return it.
     * 
     * @param is the input stream
     * @param closeStream true to close the stream when parsing is complete
     * @return the diagram created as a result of the parse
     * @throws SAXException
     */
    public ArgoDiagram readArgoDiagram(InputStream is, boolean closeStream)
        throws SAXException {

        return (ArgoDiagram) readDiagram(is, closeStream);
    }
    
    @Override
    public Diagram readDiagram(InputStream is, boolean closeStream)
        throws SAXException {
        
        // TODO: we really want to be able replace the initial content handler
        // which is passed to SAX, but we can't do this without cloning a
        // whole bunch of code because it's private in the super class.
        
        Diagram d = super.readDiagram(is, closeStream);
        
        attachEdges(d);
        
        return d;
    }
    
    /**
     * This is called when all nodes and edges have been read and placed on
     * the diagram. This method then attaches the edges to the correct node,
     * including the nodes contained within edges allowing edge to edge
     * connections for comment edges, association classes and dependencies.
     * @param d the Diagram
     */
    private void attachEdges(Diagram d) {
        for (EdgeData edgeData : figEdges) {
            final FigEdge edge = edgeData.getFigEdge();
            
            Object modelElement = modelElementsByFigEdge.get(edge);
            if (modelElement != null) {
                if (edge.getOwner() == null) {
                    edge.setOwner(modelElement);
                }
            }
        }
        
        for (EdgeData edgeData : figEdges) {
            final FigEdge edge = edgeData.getFigEdge();
            
            Fig sourcePortFig = findFig(edgeData.getSourcePortFigId());
            Fig destPortFig = findFig(edgeData.getDestPortFigId());
            final FigNode sourceFigNode =
                getFigNode(edgeData.getSourceFigNodeId());
            final FigNode destFigNode =
                getFigNode(edgeData.getDestFigNodeId());
            
            if (sourceFigNode instanceof FigEdgePort) {
                sourcePortFig = sourceFigNode;
            }
            
            if (destFigNode instanceof FigEdgePort) {
                destPortFig = destFigNode;
            }
            
            if (sourcePortFig == null && sourceFigNode != null) {
                sourcePortFig = getPortFig(sourceFigNode);
            }

            if (destPortFig == null && destFigNode != null) {
                destPortFig = getPortFig(destFigNode);
            }

            if (sourcePortFig == null
            	|| destPortFig == null 
            	|| sourceFigNode == null 
            	|| destFigNode == null) {
                LOG.error("Can't find nodes for FigEdge: "
                        + edge.getId() + ":"
                        + edge.toString());
                edge.removeFromDiagram();
            } else {
                edge.setSourcePortFig(sourcePortFig);
                edge.setDestPortFig(destPortFig);
                edge.setSourceFigNode(sourceFigNode);
                edge.setDestFigNode(destFigNode);
            }
        }
        
        // Once all edges are connected do a compute route on each to make
        // sure that annotations and the edge port is positioned correctly
        // Only do this after all edges are connected as compute route
        // requires all edges to be connected to nodes.
        // TODO: It would be nice not to have to do this and restore annotation
        // positions instead.
        for (Object edge : d.getLayer().getContentsEdgesOnly()) {
            FigEdge figEdge = (FigEdge) edge;
            figEdge.computeRouteImpl();
        }
    }
    
    // TODO: Move to GEF
    /**
     * Store data of a FigEdge together with the id's of nodes to connect to
     * @param figEdge The FigEdge
     * @param sourcePortFigId The id of the source port
     * @param destPortFigId The id of the destination port
     * @param sourceFigNodeId The id of the source node
     * @param destFigNodeId The id of the destination node
     */
    public void addFigEdge(
            final FigEdge figEdge,
            final String sourcePortFigId, 
            final String destPortFigId, 
            final String sourceFigNodeId, 
            final String destFigNodeId) {
        figEdges.add(new EdgeData(figEdge, sourcePortFigId, destPortFigId, 
                sourceFigNodeId, destFigNodeId));
    }
    
    // TODO: Move to GEF
    /**
     * Get the FigNode that the fig id represents.
     *
     * @param figId (In the form Figx.y.z)
     * @return the FigNode with the given id
     * @throws IllegalStateException
     *              if the figId supplied is not of a FigNode
     */
    private FigNode getFigNode(String figId) throws IllegalStateException {
        if (figId.contains(".")) {
            // If the id does not look like a top-level Fig then we can assume
            // that this is an id of a FigEdgePort inside some FigEdge.
            // So extract the FigEdgePort from the FigEdge and return that as
            // the FigNode.
            figId = figId.substring(0, figId.indexOf('.'));
            FigEdgeModelElement edge = (FigEdgeModelElement) findFig(figId);
            if (edge == null) {
                throw new IllegalStateException(
                        "Can't find a FigNode with id " + figId);
            }
            edge.makeEdgePort();
            return edge.getEdgePort();
        } else {
            // If there is no dot then this must be a top level Fig and can be
            // assumed to be a FigNode.
            Fig f = findFig(figId);
            if (f instanceof FigNode) {
                return (FigNode) f;
            } else {
                LOG.error("FigID " + figId + " is not a node, edge ignored");
                return null;
            }
        }
    }
    

    // TODO: Move to GEF
    /**
     * Get the Fig from the FigNode that is the port.
     *
     * @param figNode the FigNode
     * @return the Fig that is the port on the given FigNode
     */
    private Fig getPortFig(FigNode figNode) {
        if (figNode instanceof FigEdgePort) {
            // TODO: Can we just do this every time, no need for else - Bob
            return figNode;
        } else {
            return (Fig) figNode.getPortFigs().get(0);
        }
    }
    
    // TODO: Move to GEF
    
    /**
     * The data from an edge extracted from the PGML before we can guarantee
     * all the nodes have been constructed. This stores the FigEdge and the
     * id's of the nodes to connect to later.
     * If the nodes are not known then the ports are returned instead.
     */
    private class EdgeData {
        private final FigEdge figEdge;
        private final String sourcePortFigId;
        private final String destPortFigId;
        private final String sourceFigNodeId;
        private final String destFigNodeId;
        
        /**
         * Constructor
         * @param edge The FigEdge
         * @param sourcePortId The id of the source port
         * @param destPortId The id of the destination port
         * @param sourceNodeId The id of the source node
         * @param destNodeId The id of the destination node
         */
        public EdgeData(FigEdge edge, String sourcePortId, 
                String destPortId, String sourceNodeId, String destNodeId) {
            if (sourcePortId == null || destPortId == null) {
                throw new IllegalArgumentException(
                        "source port and dest port must not be null"
                        + " source = " + sourcePortId
                        + " dest = " + destPortId
                        + " figEdge = " + edge);
            }
            this.figEdge = edge;
            this.sourcePortFigId = sourcePortId;
            this.destPortFigId = destPortId;
            this.sourceFigNodeId =
                sourceNodeId != null ? sourceNodeId : sourcePortId;
            this.destFigNodeId = 
                destNodeId != null ? destNodeId : destPortId;
        }
        
        /**
         * Get the id of the destination FigNode
         * @return the id
         */
        public String getDestFigNodeId() {
            return destFigNodeId;
        }
        
        /**
         * Get the id of the destination port
         * @return the id
         */
        public String getDestPortFigId() {
            return destPortFigId;
        }
        /**
         * Get the FigEdge
         * @return the FigEdge
         */
        public FigEdge getFigEdge() {
            return figEdge;
        }
        /**
         * Get the id of the source FigNode
         * @return the id
         */
        public String getSourceFigNodeId() {
            return sourceFigNodeId;
        }
        /**
         * Get the id of the source port
         * @return the id
         */
        public String getSourcePortFigId() {
            return sourcePortFigId;
        }
    }

    /**
     * Construct a new instance of the named Fig with the owner represented
     * by the given href and the bounds parsed from the PGML file.  We look
     * for constructors of the form Fig(Object owner, Rectangle
     * bounds, DiagramSettings settings) which is typically used for subclasses
     * of FigNodeModelElement, then Fig(Object owner, DiagramSettings settings)
     * which is used for subclasses of FigEdgeModelElement.
     * <p>
     * If we fail to find any of the constructors that we know about, we'll
     * call GEF's version of this method to see if it can find a constructor.
     * 
     * @param className fully qualified name of class to instantiate
     * @param href string representing UUID of owning element
     * @param bounds position and size of figure
     * @return
     * @throws SAXException
     * @see org.tigris.gef.persistence.pgml.PGMLStackParser#constructFig(java.lang.String, java.lang.String, java.awt.Rectangle)
     */
    @Override
    protected Fig constructFig(String className, String href, Rectangle bounds)
        throws SAXException {
	
	Fig f = null;

        try {
            Class figClass = Class.forName(className);
            for (Constructor constructor : figClass.getConstructors()) {
                Class[] parameterTypes = constructor.getParameterTypes();
                // FigNodeModelElements should match here
                if (parameterTypes.length == 3
                        && parameterTypes[0].equals(Object.class)
                        && parameterTypes[1].equals(Rectangle.class)
                        && parameterTypes[2].equals(DiagramSettings.class)
                ) {
                    Object parameters[] = new Object[3];
                    Object owner = null;
                    if (href != null) {
                        owner = findOwner(href);
                    }
                    parameters[0] = owner;
                    parameters[1] = bounds;
                    parameters[2] = 
                        ((ArgoDiagram) getDiagram()).getDiagramSettings();

                    f =  (Fig) constructor.newInstance(parameters);
                }
                // FigEdgeModelElements should match here (they have no bounds)
                if (parameterTypes.length == 2
                        && parameterTypes[0].equals(Object.class)
                        && parameterTypes[1].equals(DiagramSettings.class)
                ) {
                    Object parameters[] = new Object[2];
                    Object owner = null;
                    if (href != null) {
                        owner = findOwner(href);
                    }
                    parameters[0] = owner;
                    parameters[1] = 
                        ((ArgoDiagram) getDiagram()).getDiagramSettings();

                    f =  (Fig) constructor.newInstance(parameters);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SAXException(e);
        } catch (IllegalAccessException e) {
            throw new SAXException(e);
        } catch (InstantiationException e) {
            throw new SAXException(e);
        } catch (InvocationTargetException e) {
            throw new SAXException(e);
        }

        // Fall back to GEF's handling if we couldn't find an appropriate
        // constructor
        if (f == null) {
            // TODO: Convert this to a warning or error when all the Figs
            // have been upgraded.
            LOG.debug("No ArgoUML constructor found for " + className
                    + " falling back to GEF's default constructors");
            f = super.constructFig(className, href, bounds);
        }
        
	return f;
    }
    
    /**
     * Save the newly created Diagram for use by the parser.  We take the 
     * opportunity to attach our default diagram settings to it so we'll have
     * them if needed when constructing Figs.
     * <p>
     * Diagrams are created in GEF's PGMLHandler.initDiagram() which is private
     * and can't be overridden.  Initialization sequence is:<ul> 
     * <li>load diagram class using name in PGML file
     * <li>instantiate using 0-arg constructor
     * <li>invoke this method (setDiagram(<newDiagramInstance))
     * <li>invoke diagram's initialize(Object owner) method
     * <li>diagram.setName()
     * <li>diagram.setScale()
     * <li>diagram.setShowSingleMultiplicity() 
     *     (?!why does GEF care about multiplicity?!)
     * </ul>
     * @param diagram the new diagram
     * @see org.tigris.gef.persistence.pgml.PGMLStackParser#setDiagram(org.tigris.gef.base.Diagram)
     */
    @Override
    public void setDiagram(Diagram diagram) {
        // TODO: We could generalize this to initialize more stuff if needed
        ((ArgoDiagram) diagram).setDiagramSettings(diagramSettings);
        super.setDiagram(diagram);
    }
    
    public DiagramSettings getDiagramSettings() {
        return diagramSettings;
    }
}
