/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2009 Mario Lang  All Rights Reserved.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details (a copy is included in the LICENSE.txt file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License 
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This file is maintained by Mario Lang <mlang@delysid.org>.
 */
package freedots.musicxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import freedots.model.Accidental;
import freedots.model.Articulation;
import freedots.model.AugmentedFraction;
import freedots.model.Clef;
import freedots.model.Event;
import freedots.model.Fermata;
import freedots.model.Fingering;
import freedots.model.Fraction;
import freedots.model.KeySignature;
import freedots.model.Ornament;
import freedots.model.Staff;
import freedots.model.RhythmicElement;
import freedots.model.Syllabic;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class Note extends Musicdata implements RhythmicElement {
  static final String ACCIDENTAL_ELEMENT = "accidental";
  static final String CHORD_ELEMENT = "chord";
  static final String LYRIC_ELEMENT = "lyric";
  static final String NOTATIONS_ELEMENT = "notations";
  static final String STAFF_ELEMENT = "staff";
  static final String TIME_MODIFICATION_ELEMENT = "time-modification";

  Part part;
  public Part getPart() { return part; }

  Fraction offset;
  Staff staff = null;

  Element grace = null;
  Pitch pitch = null;
  Text duration = null;

  Text staffNumber, voiceName;
  Type type = Type.NONE;
  private final static Map<String, Type>
  typeMap = Collections.unmodifiableMap(new HashMap<String, Type>() {
      {
        put("long", Type.LONG);
        put("breve", Type.BREVE);
        put("whole", Type.WHOLE);
        put("half", Type.HALF);
        put("quarter", Type.QUARTER);
        put("eighth", Type.EIGHTH);
        put("16th", Type.SIXTEENTH);
        put("32nd", Type.THIRTYSECOND);
        put("64th", Type.SIXTYFOURTH);
        put("128th", Type.ONEHUNDREDTWENTYEIGHTH);
        put("256th", Type.TWOHUNDREDFIFTYSIXTH);
      }
    });
  Accidental accidental = null;
  private final static Map<String, Accidental>
  accidentalMap = Collections.unmodifiableMap(new HashMap<String, Accidental>() {
      { put("natural", Accidental.NATURAL);
        put("flat", Accidental.FLAT);
        put("flat-flat", Accidental.DOUBLE_FLAT);
        put("sharp", Accidental.SHARP);
        put("sharp-sharp", Accidental.DOUBLE_SHARP);
        put("double-sharp", Accidental.DOUBLE_SHARP);
      }
    });

  Element tie = null;
  Element timeModification = null;

  Lyric lyric = null;

  Notations notations = null;

  Note(
    Fraction offset, Element element,
    int divisions, int durationMultiplier,
    Part part
  ) throws MusicXMLParseException {
    super(element, divisions, durationMultiplier);
    this.part = part;
    this.offset = offset;
    for (Node node = element.getFirstChild(); node != null;
         node = node.getNextSibling()) {
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element)node;
        if (child.getNodeName().equals("grace")) {
          grace = child;
        } else if (child.getNodeName().equals("pitch")) {
          pitch = new Pitch(child);
        } else if (child.getNodeName().equals("duration")) {
          duration = firstTextNode(child);
        } else if (child.getNodeName().equals("tie")) {
          tie = child;
        } else if (child.getNodeName().equals("voice")) {
          voiceName = firstTextNode(child);
        } else if (child.getNodeName().equals("type")) {
          Text textNode = firstTextNode(child);
          if (textNode != null) {
            String typeName = textNode.getWholeText();
            String santizedTypeName = typeName.trim().toLowerCase();
            if (typeMap.containsKey(santizedTypeName))
              type = typeMap.get(santizedTypeName);
            else
              throw new MusicXMLParseException("Illegal <type> content '"+typeName+"'");
          }
        } else if (child.getNodeName().equals(ACCIDENTAL_ELEMENT)) {
          if (part.getScore().encodingSupports(ACCIDENTAL_ELEMENT)) {
            Text textNode = firstTextNode(child);
            if (textNode != null) {
              String accidentalName = textNode.getWholeText();
              String santizedName = accidentalName.trim().toLowerCase();
              if (accidentalMap.containsKey(santizedName))
                accidental = accidentalMap.get(santizedName);
              else
                throw new MusicXMLParseException("Illegal <accidental>"+accidentalName+"</accidental>");
            }
          }
        } else if (child.getNodeName().equals(TIME_MODIFICATION_ELEMENT)) {
          timeModification = child;
        } else if (child.getNodeName().equals(STAFF_ELEMENT)) {
          staffNumber = firstTextNode(child);
        } else if (child.getNodeName().equals(NOTATIONS_ELEMENT)) {
          notations = new Notations(child);
        } else if (child.getNodeName().equals(LYRIC_ELEMENT)) {
          lyric = new Lyric(child);
        }
      }
    }
  }

  static Text firstTextNode(Node node) {
    for (Node child = node.getFirstChild(); node != null;
         node = node.getNextSibling()) {
      if (child.getNodeType() == Node.TEXT_NODE) return (Text)child;
    }
    return null;
  }

  public boolean isGrace() {
    return (grace != null);
  }
  public boolean isRest() {
    if ("forward".equals(element.getTagName()) ||
        element.getElementsByTagName("rest").getLength() > 0)
      return true;
    return false;
  }
  public Pitch getPitch() { return pitch; }
  public int getStaffNumber() {
    if (staffNumber != null) {
      return Integer.parseInt(staffNumber.getWholeText()) - 1;
    }
    return 0;
  }
  public String getVoiceName() {
    if (voiceName != null) {
      return voiceName.getWholeText();
    }
    return null;
  }
  public void setVoiceName(String name) {
    if (voiceName != null) {
      voiceName.replaceWholeText(name);
    }
  }

  public AugmentedFraction getAugmentedFraction() {
    if (type != Type.NONE) {
      int normalNotes = 1;
      int actualNotes = 1;
      if (timeModification != null) {
        normalNotes = Integer.parseInt(Score.getTextNode(timeModification, "normal-notes").getWholeText());
        actualNotes = Integer.parseInt(Score.getTextNode(timeModification, "actual-notes").getWholeText());
      }
      return new AugmentedFraction(type.getNumerator(), type.getDenominator(),
                                   element.getElementsByTagName("dot").getLength(),
                                   normalNotes, actualNotes);
    } else {
      return new AugmentedFraction(getDuration());
    }
  }

  public Accidental getAccidental() {
    return accidental;
  }
  public void setAccidental(Accidental accidental) {
    this.accidental = accidental;

    if (part.getScore().encodingSupports(ACCIDENTAL_ELEMENT)) {
      String accidentalName = null;
      if (accidental != null) {
        if (accidental.getAlter() == 0) accidentalName = "natural";
        else if (accidental.getAlter() == -1) accidentalName = "flat";
        else if (accidental.getAlter() == 1) accidentalName = "sharp";
      }

      Node node;
      for (node = element.getFirstChild(); node != null;
           node = node.getNextSibling()) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          if (node.getNodeName().equals(ACCIDENTAL_ELEMENT)) {
            if (accidental != null) {
              if (accidentalName != null) node.setTextContent(accidentalName);
            } else {
              element.removeChild(node);
            }
            return;
          }
        }
      }

      for (node = element.getFirstChild(); node != null;
           node = node.getNextSibling()) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          if (node.getNodeName().equals(TIME_MODIFICATION_ELEMENT)
           || node.getNodeName().equals("stem")
           || node.getNodeName().equals("notehead")
           || node.getNodeName().equals(STAFF_ELEMENT)
           || node.getNodeName().equals("beam")
           || node.getNodeName().equals(NOTATIONS_ELEMENT)
           || node.getNodeName().equals(LYRIC_ELEMENT)) break;
        }
      }
      Element
      newElement = element.getOwnerDocument().createElement(ACCIDENTAL_ELEMENT);
      newElement.setTextContent(accidentalName);
      element.insertBefore(newElement, node);
    }
  }

  public boolean isTieStart() {
    if (tie != null && tie.getAttribute("type").equals("start")) return true;
    return false;
  }

  public Fraction getOffset() { return offset; }
  public Staff getStaff() { return staff; }
  public void setStaff(Staff staff) { this.staff = staff; }

  enum Type {
    LONG(4, 1), BREVE(2, 1), WHOLE(1, 1), HALF(1, 2), QUARTER(1, 4),
    EIGHTH(1, 8), SIXTEENTH(1, 16), THIRTYSECOND(1, 32),
    SIXTYFOURTH(1, 64), ONEHUNDREDTWENTYEIGHTH(1, 128),
    TWOHUNDREDFIFTYSIXTH(1, 256), NONE(0, 1);

    int numerator;
    int denominator;
    private Type(int numerator, int denominator) {
      this.numerator = numerator;
      this.denominator = denominator;
    }
    int getNumerator() { return numerator; }
    int getDenominator() { return denominator; }      
  }

  class Lyric implements freedots.model.Lyric {
    Element element;

    Lyric(Element element) {
      this.element = element;
    }
    public String getText() {
      Text textNode = Score.getTextNode(element, "text");
      if (textNode != null) return textNode.getWholeText();
      return "";
    }
    public Syllabic getSyllabic() {
      Text textNode = Score.getTextNode(element, "syllabic");
      if (textNode != null) {      
        return Enum.valueOf(Syllabic.class, textNode.getWholeText().toUpperCase());
      }
      return null;
    }
  }
  public Lyric getLyric() { return lyric; }

  public boolean equalsIgnoreOffset(Event object) {
    if (object instanceof Note) {
      Note other = (Note)object;

      if (getAugmentedFraction().equals(other.getAugmentedFraction())) {
        if (getAccidental() == other.getAccidental()) {
          if (getPitch() == null) {
            return (other.getPitch() == null);
          } else {
            return getPitch().equals(other.getPitch());
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Note) {
      Note other = (Note)object;

      if (getOffset().equals(other.getOffset())) {
        return equalsIgnoreOffset(other);
      }
    }
    return false;
  }

  public int getMidiChannel() {
    MidiInstrument instrument = part.getMidiInstrument(null);
    if (instrument != null) return instrument.getMidiChannel();
    return 0;
  }
  public int getMidiProgram() {
    MidiInstrument instrument = part.getMidiInstrument(null);
    if (instrument != null) return instrument.getMidiProgram();
    return 0;
  }
  @Override
  public Fraction getDuration() throws MusicXMLParseException {
    if (duration != null) {
      int value = Math.round(Float.parseFloat(duration.getNodeValue()));
      Fraction fraction = new Fraction(value * durationMultiplier,
                                       4 * divisions);
      return fraction;
    }
    return getAugmentedFraction().basicFraction(); 
  }

  public Notations getNotations() { return notations; }

  private List<Slur> slurs = new ArrayList<Slur>(2);

  public List<Slur> getSlurs() { return slurs; }
  public void addSlur(Slur slur) { slurs.add(slur); }

  public Fingering getFingering() {
    if (notations != null) {
      Notations.Technical technical = notations.getTechnical();
      if (technical != null) {
        return technical.getFingering();
      }
    }

    return null;
  }
  public void setFingering(Fingering fingering) {
    if (notations != null) {
      Notations.Technical technical = notations.getTechnical();
      if (technical == null) {
        technical = notations.createTechnical();
      }
      technical.setFingering(fingering);
    }
  }


  public Fermata getFermata() {
    if (notations != null) {
      return notations.getFermata();
    }

    return null;
  }
  public Set<Articulation> getArticulations() {
    if (notations != null) {
      return notations.getArticulations();
    }

    return null;
  }
  public Set<Ornament> getOrnaments() {
    if (notations != null) {
      return notations.getOrnaments();
    }

    return null;
  }
  public Clef getClef() {
    if (staff != null) {
      return staff.getClef(offset);
    }
    return null;
  }
  public KeySignature getActiveKeySignature() {
    if (staff != null) {
      return staff.getKeySignature(offset);
    }
    return null;
  }
}