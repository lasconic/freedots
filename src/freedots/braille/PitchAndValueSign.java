/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2010 Mario Lang  All Rights Reserved.
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
package freedots.braille;

import freedots.music.AbstractPitch;
import freedots.music.AugmentedPowerOfTwo;

/** Shows both the pitch and the rhythmic length of a note.
 */
public class PitchAndValueSign extends Sign {
  private final AbstractPitch pitch;
  private final AugmentedPowerOfTwo value;

  PitchAndValueSign(final AbstractPitch pitch,
                    final AugmentedPowerOfTwo value) {
    super(getSign(pitch, value));
    this.pitch = pitch;
    this.value = value;
  }

  public String getDescription() {
    return "A " + pitch.toString() + " with duration " + value.toString();
  }

  private static String getSign(final AbstractPitch pitch,
                                final AugmentedPowerOfTwo value) {
    final int power = value.getPower();
    // FIXME: breve and long notes are not handled at all
    final int log = Math.abs(power);
    final int valueType = log > Math.abs(AugmentedPowerOfTwo.QUAVER.getPower())
      ? log+AugmentedPowerOfTwo.SEMIQUAVER.getPower() : log;

    return String.valueOf((char)(UNICODE_BRAILLE_MASK
                                 | STEP_BITS[pitch.getStep()]
                                 | DENOMINATOR_BITS[valueType]));
  }
  private static final int[] STEP_BITS = {
    dotsToBits(145), dotsToBits(15), dotsToBits(124), dotsToBits(1245),
    dotsToBits(125), dotsToBits(24), dotsToBits(245)
  };
  private static final int[] DENOMINATOR_BITS = {
    dotsToBits(36), dotsToBits(3), dotsToBits(6), 0
  };
}
