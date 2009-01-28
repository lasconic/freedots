/* -*- c-basic-offset: 2; -*- */
package org.delysid.freedots.musicxml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Barline {
  Element element;

  public Barline(Element element) { this.element = element; }
  public Location getLocation() {
    String attrValue = element.getAttribute("location").toUpperCase();
    if (attrValue.length() > 0)
      return Enum.valueOf(Location.class, attrValue);
    return Location.RIGHT;
  }
  public Repeat getRepeat() {
    NodeList nodeList = element.getElementsByTagName("repeat");
    if (nodeList.getLength() >= 1) {
      Element repeat = (Element)nodeList.item(nodeList.getLength()-1);
      return Enum.valueOf(Repeat.class, repeat.getAttribute("direction").toUpperCase());
    }
    return null;
  }
  public enum Location { LEFT, RIGHT, MIDDLE; }
  public enum Repeat { BACKWARD, FORWARD; }
}