package Assingment
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import akka.routing.RoundRobinPool

import scala.io.Source
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.stream.{ActorAttributes, ActorMaterializer, IOResult}
import scala.util.{Failure, Success, Try}

object AkkaAssingment extends App{

  case class Record(var OrderDate: String,
                    Shipingdate: String,
                    ShipingMode: String,
                    Customer: String,
                    Segment: String,
                    Country: String,
                    City: String,
                    State: String,
                    Region: String,
                    Category: String,
                    SubCategory: String,
                    Name: String,
                    Sales: String,
                    Quanity: String,
                    Discount: String,
                    Profit: String)

  class FileReaderActorClass extends Actor {

    val childRouter: ActorRef = context.actorOf(RoundRobinPool(10).props(Props[ChildActor]), "childRouter")
    val errorHandler: ActorRef = context.actorOf(Props[ErrorHandler], "errorHandler")

    override def receive: Receive = {
      case record: Array[String] =>
        childRouter ! record
      case Terminated(child) =>
        println(s"Child actor ${child.path.name} terminated")

    }
  }

  val filepath = "resources/Superstore_purchases.csv"

  val source = Source.fromFile(filepath)
  val lines = source.getLines()

  val system = ActorSystem("FileReaderSystem")
  val fileReaderActor = system.actorOf(Props[FileReaderActorClass], "fileReaderActor")

  for (line <- lines) {
    val record = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?<![ ])(?![ ])")
    fileReaderActor ! record
  }



  val categorysinkfile = "C:/Assingments/furnitures.csv"

  class ChildActor extends Actor {
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    override def receive: Receive = {
      case record: Array[String] =>

        val result = Try(Record(record(0), record(1), record(2), record(3), record(4), record(5), record(6), record(7), record(8), record(9), record(10), record(11), record(12), record(13), record(14), record(15)))
        result match {
          case Success(data) =>
            if(validation(record)) {
              val source=Source.single(data)

              val sink=Sink.foreach(println)

              //source.to(sink).run()



            }
          case Failure(ex) =>
            context.actorSelection("errorHandler") ! ex
        }
    }

  }

  class ErrorHandler extends Actor {
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    override def receive: Receive = {
      case ex: Throwable =>
        Source.single(ex).runWith(Sink.foreach(println))
    }

  }

  def validation(record:Array[String]):Boolean= {
    for(i<-record)
    {
      if(i==""||i==null)
      {
        return false
      }
    }
    return true
  }
}


