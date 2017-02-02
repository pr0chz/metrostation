import java.io.{FileInputStream, InputStream}
import java.util.Scanner

import cz.prochy.metrostation.tracking.PragueStations
import org.json.simple.{JSONObject, JSONValue}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Snake {

  case class Location(cid:Int, lac:Int)
  case class Event(id:Long, ts:Long, loc:Option[Location]) extends Ordered[Event] {
    override def compare(that: Event): Int = ts compare that.ts
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

  // TODO
  // refactor Stations to provide better suited data for analysis
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
  }

}
