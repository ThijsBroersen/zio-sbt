package zio.sbt

sealed trait ScalaPlatform {
  def asString: String = this match {
    case ScalaPlatform.JS     => "JS"
    case ScalaPlatform.JVM    => "JVM"
    case ScalaPlatform.Native => "Native"
  }
}
object ScalaPlatform {
  case object JS     extends ScalaPlatform
  case object JVM    extends ScalaPlatform
  case object Native extends ScalaPlatform
}
