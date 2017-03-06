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

import java.io._
import java.nio.file.{Files, Path, Paths}
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.{Date, Scanner}

import cz.prochy.metrostation.tracking.PragueStations
import cz.prochy.metrostation.tracking.internal.graph.StationGraph
import org.json.simple.{JSONObject, JSONValue}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable

object Snake {

  case class Location(cid:Int, lac:Int) {
    def valid:Boolean = !(cid == 0 || lac == 0 || cid == 2147483647 || lac == 2147483647)
  }
  case class Event(id:Long, ts:Long, loc:Option[Location]) extends Ordered[Event] {
    def date:String = Event.format.format(new Date(ts))
    override def compare(that: Event): Int = ts compare that.ts
  }
  object Event {
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
  }


  def readInput(is:InputStream):IndexedSeq[Event] = {
    def parseEvent(s:String):Event = {
      val json = JSONValue.parse(s).asInstanceOf[JSONObject]

      val id = json.get("id").asInstanceOf[Long]
      val ts = json.get("ts").asInstanceOf[Long]

      val location = if (json.containsKey("cid")) {
        val cid = json.get("cid").asInstanceOf[Long].toInt
        val lac = json.get("lac").asInstanceOf[Long].toInt
        Some(Location(cid, lac))
      } else {
        None
      }

      Event(id, ts, location)
    }

    val scanner = new Scanner(is)
    val output = mutable.ArrayBuffer[Event]()
    while (scanner.hasNextLine) {
      val event = parseEvent(scanner.nextLine())
      output.append(event)
    }
    output
  }

  val stations:StationGraph = PragueStations.newGraph

  val graphBuilder:AnalysisGraphBuilder = new AnalysisGraphBuilder
  PragueStations.buildStations(graphBuilder)
  val cells:Seq[CellInfo] = graphBuilder.build
  val cellsByCid:Map[(Int, Int), Seq[CellInfo]] = cells
    .groupBy { case CellInfo(_, _, _, cid, lac) => (cid, lac) }
    .withDefaultValue(Seq())


  def hasMetroStation(events:IndexedSeq[Event]): Boolean = {
    val nonEmptyCells = for {
      event <- events
      loc <- event.loc
    } yield !stations.getStations(loc.cid, loc.lac).isEmpty
    nonEmptyCells.exists(identity)
  }

  def enumerateStations(snakes:Iterable[Iterable[Event]]):Set[String] = {
    val stationNames = for {
      snake <- snakes
      event <- snake
      location <- event.loc.toIterable
      station <- stations.getStations(location.cid, location.lac).asSet.asScala
    } yield station.getName
    stationNames.toSet
  }

  def coverageReport(events:Iterable[Event]):Unit = {

    def extractCi(keySet:Set[(Int, Int)]): Seq[CellInfo] = {
      for {
        key <- keySet.toSeq
        ci <- cellsByCid(key)
      } yield ci
    }

    def ratio(a:Seq[CellInfo], total:Seq[CellInfo], condition:(CellInfo) => Boolean):Double = {
      100 * a.count(condition).toDouble / total.count(condition)
    }

    val eventSetByCid = (for {
      event <- events
      loc <- event.loc.toIterable
    } yield (loc.cid, loc.lac)).toSet

    val presentCids = extractCi(cellsByCid.keySet & eventSetByCid)
    val missingCids = extractCi(cellsByCid.keySet -- eventSetByCid)

    val carriers = cells.map(ci => (ci.line, ci.carrier)).distinct
    println("\nCarrier coverage:")
    for ((line, carrier) <- carriers) {
      println(f"$line%s $carrier%10s: ${ratio(presentCids, cells, ci => ci.carrier == carrier && ci.line == line)}%2.2f%%")
    }

    // sort (carrier, cid) sorted by incidence count
    val cellIndicence = events
      .collect { case Event(_, _, location) => location }
      .flatten
      .groupBy(identity)
      .mapValues(x => x.size)
      .toSeq
      .sortBy { case (_, c) => c }
      .reverse

    println("\nCells (most occurences first)")
    for {
      (Location(cid, lac), count) <- cellIndicence
      cell <- cellsByCid.get(cid, lac)
    } println(f"$count%5d: $cell")
  }

  def collect[T](events:Seq[Event], what:CellInfo => T):String = {
    (
      for {
        event <- events
        loc <- event.loc.toIterable
        cellInfo <- cellsByCid(loc.cid, loc.lac)
      } yield what(cellInfo)
    ).toSet.mkString
  }

  def niceCarrierName(name:String):String = name match {
    case "t-mobile" => "TM"
    case "o2" => "O2"
    case "vodafone" => "VF"
    case "unknown" => "U"
    case x => x
  }

  def drawsnake(id:String, events:Seq[Event]): Unit = {
    print(f"$id%15s ")
    print(f"${collect(events, ci => niceCarrierName(ci.carrier))}%-5s")
    print(f"${collect(events, ci => ci.line)}%-4s")
    print(": ")
    var lastLoc:Option[Snake.Location] = None
    var unknowns = 0
    for (ev <- events) {
      val char = if (lastLoc == ev.loc) {
        "" // deduplicate
      } else {
        if (ev.loc.isEmpty) {
          unknowns = 0
          "|"
        } else {
          val loc = ev.loc.get
          if (!loc.valid) {
            unknowns = 0
            "/"
          } else {
            val group = stations.getStations(loc.cid, loc.lac)
            if (group.isEmpty) {
              unknowns += 1
              if (unknowns < 5) "_" else ""
            } else {
              unknowns = 0
              "."
            }
          }
        }
      }
      lastLoc = ev.loc
      print(char)
    }
    println()
  }

  def dumpSnake(events:Seq[Event]): String = {
    val eventLines = for {
      firstEv <- events.headOption.toIterable
      ev <- events
    } yield {
      val relTs = f"[${ev.ts - firstEv.ts}%10d]"
      val time = f"[${ev.date}%15s]"
      val cid = ev.loc.map(loc => f"connected(${loc.cid}%d, ${loc.lac}%d)").getOrElse("disconnected")
      val stationStr = ev.loc.toIterable
        .map(loc => stations.getStations(loc.cid, loc.lac))
        .flatMap(stationGroup => stationGroup.asSet().asScala)
        .map(_.getName)
        .mkString(",")
      val carrier = ev.loc.toIterable
        .flatMap(loc => cellsByCid(loc.cid, loc.lac))
        .map(_.carrier)
        .map(niceCarrierName)
        .mkString(",")
      f"$time%s $relTs%s - $carrier%5s - $stationStr%20s - $cid%s"
    }
    eventLines.mkString("\n")
  }

  def splitBetween[T, C <: Seq[T]](c:C, split: (T, T) => Boolean): Seq[Seq[T]] = {

    @tailrec
    def takeUntil(c:List[T], acc:List[T] = Nil): (List[T], List[T]) = c match {
      case Nil => (acc.reverse, Nil)
      case x :: Nil => ((x :: acc).reverse, Nil)
      case x :: tail =>
        val next = tail.head
        if (split(x, next)) ((x :: acc).reverse, tail) else takeUntil(tail, x :: acc)
    }

    @tailrec
    def splitAll(c:List[T], acc:List[List[T]] = Nil): List[List[T]] = {
      if (c.isEmpty) acc.reverse
      else {
        val (part, tail) = takeUntil(c)
        splitAll(tail, part :: acc)
      }
    }

    splitAll(c.toList)
  }

  def filterInterestingEvents(events:Seq[Event], maxDelayM:Long): Seq[Seq[Event]] = {
    val maxMs = TimeUnit.MINUTES.toMillis(maxDelayM)

    def longPause(delayMs:Long)(x:Event, y:Event) = y.ts - x.ts > delayMs
    def disconnect(ev:Event) = ev.loc.isEmpty

    val splits = splitBetween(events.filter(disconnect), longPause(maxMs))
    for {
      split <- splits
      if split.size > 3
    } yield {
      val tss = split.map(_.ts)
      val lower = tss.min - maxMs
      val upper = tss.max + maxMs
      events.filter(e => e.ts > lower && e.ts < upper)
    }
  }

  def main(args: Array[String]): Unit = {
    val is = if (!args.isEmpty) new FileInputStream(args(0)) else System.in
    val events = readInput(is)
    val snakes:Map[Long, IndexedSeq[Event]] = events groupBy { case Event(id, _, _) => id } mapValues { evs => evs.sorted }

    println(s"Total snakes ${snakes.size}")
    println(enumerateStations(snakes.values).toArray.sorted)

    val snakesWithMetro = snakes filter { case (_, snakeEvents) => hasMetroStation(snakeEvents) }
    println(s"Snakes with station ${snakesWithMetro.size}")


    coverageReport(events)

    snakesWithMetro.foreach { case (id, seq) => drawsnake(id.toString, seq) }
    println("============")

    val dir = "snakefiles"
    Files.createDirectories(Paths.get(dir))
    for ((id, seq) <- snakesWithMetro) {
      val os = Files.newOutputStream(Paths.get(dir, id.toString))
      val writer = new PrintWriter(os)
      for ((events, i) <- filterInterestingEvents(snakesWithMetro(id), 15).zipWithIndex) {
        drawsnake(s"$id $i", events)
        writer.write("=========\n")
        writer.write(dumpSnake(events))
        writer.write("\n")
      }
      writer.write("\n\n===== Full dump =====\n")
      writer.write(dumpSnake(seq))
      writer.close()
    }
  }

}
