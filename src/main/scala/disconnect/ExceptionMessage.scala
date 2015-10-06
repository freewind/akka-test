package disconnect

sealed trait ExceptionMessage extends Serializable

case class MyException(content : String) extends ExceptionMessage
