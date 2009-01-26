/* -*- c-basic-offset: 2; -*- */
package org.delysid.freedots.model;

public final class
VoiceChord extends AbstractChord<VoiceElement> implements VoiceElement {
  private String staffName;
  private String voiceName;

  public VoiceChord(VoiceElement initialNote) {
    super(initialNote);
    this.staffName = initialNote.getStaffName();
    this.voiceName = initialNote.getVoiceName();
    this.staff = initialNote.getStaff();
  }

  private Staff staff = null;

  public Staff getStaff() { return staff; }
  public void setStaff(Staff staff) { this.staff = staff; }
  public String getStaffName() { return staffName; }

  /* FIXME: Seems inappropriate to have to impelement this */
  public boolean isRest() { return false; }

  public String getVoiceName() { return voiceName; }
  public void setVoiceName(String voiceName) {
    this.voiceName = voiceName;
    for (VoiceElement element:this) { element.setVoiceName(voiceName); }
  }
}
