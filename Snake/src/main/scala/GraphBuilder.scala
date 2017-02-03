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
