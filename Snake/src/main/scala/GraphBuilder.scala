/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.metrostation.snake

import cz.prochy.metrostation.tracking.graph.{GraphBuilder, LineBuilder, StationBuilder}

import scala.collection.mutable

case class CellInfo(val line:String, val name:String, val carrier:String, val cid:Int, val lac:Int)

class AnalysisGraphBuilder extends GraphBuilder[Seq[CellInfo]] {
  val buffer: mutable.ArrayBuffer[CellInfo] = mutable.ArrayBuffer()

  private class LB(val line:String) extends LineBuilder {
    override def station(name: String): StationBuilder = new SB(name)

    private class SB(val station:String) extends StationBuilder {
      override def id(op: String, cid: Int, lac: Int): StationBuilder = {
        buffer += CellInfo(line, station, op, cid, lac)
        this
      }
    }
  }

  override def newLine(name: String): LineBuilder = new LB(name)

  override def build(): Seq[CellInfo] = buffer
}
