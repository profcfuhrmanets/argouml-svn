// Copyright (c) 1996-98 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation for educational, research and non-profit
// purposes, without fee, and without a written agreement is hereby granted,
// provided that the above copyright notice and this paragraph appear in all
// copies. Permission to incorporate this software into commercial products
// must be negotiated with University of California. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "as is",
// without any accompanying services from The Regents. The Regents do not
// warrant that the operation of the program will be uninterrupted or
// error-free. The end-user understands that the program was developed for
// research purposes and is advised not to rely exclusively on the program for
// any reason. IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY
// PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
// INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS
// DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY
// DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE
// SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
// ENHANCEMENTS, OR MODIFICATIONS.

package uci.uml.ocl;

import java.util.*;
import java.awt.*;
import java.lang.reflect.*;
import java.lang.*;

public class OCLEvaluator {

  public static Vector eval(Hashtable bindings, String expr) {
    int firstPos = expr.indexOf(".");
    Object target = bindings.get(expr.substring(0, firstPos));
    Vector targets;

    if (target instanceof Vector)  {
      targets = (Vector) target;
    }
    else {
      targets = new Vector();
      targets.addElement(target);
    }
    String prop = expr.substring(firstPos);
    return eval(bindings, prop, targets);
  } // end of eval()

  public static Vector eval(Hashtable bindings, String expr, Vector targets) {
    int firstPos, secPos, numElements;
    String property;

    while (expr.length() > 0) {
      Vector v = new Vector();
      firstPos = expr.indexOf(".");
      secPos = expr.indexOf(".", firstPos + 1);

      if (secPos == -1) { // <expr>::= ".<property>"
	property = expr.substring(firstPos + 1);
	expr = "";
      } else {            // <expr>::= ".<property>.<expr>"
	property = expr.substring(firstPos + 1, secPos);
	expr = expr.substring(secPos); //+1
      }
      numElements = targets.size();
      for (int i = 0; i < numElements; i++) {
	v.addElement(evaluateProperty(targets.elementAt(i), property));
      }
      targets = flatten(v);
      // the results of evaluating a property may result in a vector
    }
    return targets;
  } // end of eval()

  public static String toTitleCase(String s) {
    if ( s.length() > 1 ) {
      return s.substring(0, 1).toUpperCase()
+ s.substring (1, s.length() );
    } else {
      return s.toUpperCase();
    }
  } // end of toTitleCase


  public static Object evaluateProperty(Object target, String property) {
    if (target == null) return null;
    Method m = null;
    Field  f = null;
    Object o = null;

    try {
      m = target.getClass().getMethod("get" + toTitleCase(property), null);
      o = m.invoke(target, null); // getter methods take no args =>  null
      //            System.out.println("Trying to get method " + toTitleCase(property));
      return  o;
    }
    catch ( NoSuchMethodException e ) {}
    catch ( InvocationTargetException e ) {
      if (m != null) {
	System.out.println("On Class: " + target.getClass().getName());
	System.out.println("error in evaluating " + "get" +
			   toTitleCase(property) + "()");
	e.getTargetException().printStackTrace();
	return null;
      }
    }
    catch ( Exception e ) { }

    try {
      m = target.getClass().getMethod( property, null);
      o = m.invoke(target, null);
      //            System.out.println("Trying to get method " + toTitleCase(property));
      return o;
    }
    catch ( NoSuchMethodException e ) {}
    catch ( InvocationTargetException e ) {
      if (m != null) {
	System.out.println("On Class: " + target.getClass().getName());
	System.out.println("error in evaluating " + property + "()");
	e.getTargetException().printStackTrace();
	return null;
      }
    }
    catch ( Exception e ) { }


    try {
      m = target.getClass().getMethod( toTitleCase(property), null);
      o = m.invoke(target, null);
      //            System.out.println("Trying to get method" + property);
      return o;
    } catch ( Exception e ) {}

    try {
      f = target.getClass().getField(property);
      o = f.get(target);  // access the field f or object targe
      return o;
    }
    catch ( NoSuchFieldException e ) {
      System.out.println("On Class: " + target.getClass().getName());
      System.out.println("Trying to get field " + property);
      e.printStackTrace();
      return null;
    }
    catch ( Exception e ) {
      if (f != null) {
	System.out.println("On Class: " + target.getClass().getName());
	System.out.println("error in evaluating field " + property);
	e.printStackTrace();
	return null;
      }
    }

    return null;
  } // end of evaluateProperty


  public static Vector flatten(Vector v) {
    Vector accum = new Vector();
    flattenInto(v, accum);
    return accum;
  }


  public static void flattenInto(Object o, Vector accum) {
    if ( !(o instanceof Vector) ) {
      accum.addElement(o);
    } else {
      Enumeration e = ((Vector) o).elements();
      while (e.hasMoreElements() ) {
	flattenInto(e.nextElement(), accum);
      }
    }
  }

}  // end of OCLEvaluator
