package cz.prochy.metrostation.snake

import java.io._
import java.nio.file.{Files, Path, Paths}
import java.text.SimpleDateFormat
import java.util.{Date, Scanner}

import cz.prochy.metrostation.tracking.PragueStations
import cz.prochy.metrostation.tracking.internal.Station
import org.json.simple.{JSONObject, JSONValue}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

object Snake {

  case class Location(cid:Int, lac:Int) {
    def valid = !(cid == 0 || lac == 0 || cid == 2147483647 || lac == 2147483647)
  }
  case class Event(id:Long, ts:Long, loc:Option[Location]) extends Ordered[Event] {
    def date = Event.format.format(new Date(ts))
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

  val stations = PragueStations.newGraph

  val graphBuilder = new AnalysisGraphBuilder
  PragueStations.buildStations(graphBuilder)
  val cells = graphBuilder.build
  val cellsByCid = cells
    .groupBy { case CellInfo(_, _, _, cid, lac) => (cid, lac) }
    .withDefaultValue(Seq())


  def hasMetroStation(events:IndexedSeq[Event]): Boolean = {
    val nonEmptyCells = for {
      event <- events
      loc <- event.loc
    } yield (!stations.getStations(loc.cid, loc.lac).isEmpty)
    nonEmptyCells.exists(identity)
  }

  def enumerateStations(snakes:Iterable[Iterable[Event]]):Set[String] = {
    val stationNames = for {
      snake <- snakes
      event <- snake
      location <- event.loc.toIterable
      station <- stations.getStations(location.cid, location.lac).asSet.asScala
    } yield (station.getName)
    stationNames.toSet
  }

  def coverageReport(events:Iterable[Event]):Unit = {

    def extractCi(keySet:Set[(Int, Int)]): Seq[CellInfo] = {
      for {
        key <- keySet.toSeq
        ci <- cellsByCid(key)
      } yield (ci)
    }

    def ratio(a:Seq[CellInfo], total:Seq[CellInfo], condition:(CellInfo) => Boolean):Double = {
      return 100 * a.count(condition).toDouble / total.count(condition)
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
    } {
      println(f"${count}%5d: ${cell}")
    }
  }

  def visualizeSnakes(snakes:Map[Long, Seq[Event]]):Unit = {

    def drawsnake(id:Long, events:Seq[Event]): Unit = {
      print(f"$id%12d: ")
      var lastLoc:Option[Snake.Location] = None
      var unknowns = 0
      for (ev <- events) {
        val char = if (lastLoc == ev.loc) {
          "" // deduplicate
        } else {
          if (!ev.loc.isDefined) {
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

    snakes.foreach { case (id, seq) => drawsnake(id, seq) }
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
        .mkString(",")
      f"${time}%s ${relTs}%s - ${carrier}%10s - ${stationStr}%20s - ${cid}%s"
    }
    eventLines.mkString("\n")
  }

  def filterInterestingEvents(events:Seq[Event]): Seq[Event] = {
    def knownCell(loc:Option[Location]) = loc.map(l => !cellsByCid(l.cid, l.lac).isEmpty).getOrElse(false)
    def interesting(events: Seq[Event]) = events.exists(ev => knownCell(ev.loc))

    val interestingEvents = events.sliding(5).map(interesting).toSeq
    val first = interestingEvents.head
    val last = interestingEvents.last
    val padded = first +: first +: interestingEvents.drop(2).dropRight(2) :+ last :+ last
    padded.zip(events).collect { case (true, ev) => ev }
  }


  // TODO
  // analyze which stations are present in data on which operators (and which are not)
  // analyze which cids are not present in data at all (evalute whether we have enough data for this classification)
  // find unknown cids - go through a snake and detect unknown cids inside otherwise reasonable metro travel sequence
  // find strange situations
  //   - bad prediction
  //   - missed station(s)

  def main(args: Array[String]): Unit = {
    val is = if (!args.isEmpty) new FileInputStream(args(0)) else System.in
    val events = readInput(is)
    val snakes:Map[Long, IndexedSeq[Event]] = events groupBy { case Event(id, _, _) => id } mapValues { evs => evs.sorted }

    println(s"Total snakes ${snakes.size}")
    println(enumerateStations(snakes.values).toArray.sorted)

    val snakesWithMetro = snakes filter { case (id, events) => hasMetroStation(events) }
    println(s"Snakes with station ${snakesWithMetro.size}")


    coverageReport(events)

    visualizeSnakes(snakesWithMetro)

    val dir = "snakefiles"
    Files.createDirectories(Paths.get(dir))
    for ((id, seq) <- snakesWithMetro) {
      val os = Files.newOutputStream(Paths.get(dir, id.toString))
      val writer = new PrintWriter(os)
      writer.write(dumpSnake(snakesWithMetro(id)))
      writer.close
    }
  }

}
