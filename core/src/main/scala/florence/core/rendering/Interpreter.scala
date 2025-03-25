package florence.core.rendering

trait Interpreter[F, G]:
  def interpret(source: F): G

object Interpreters:
  def interpret[F, G](source: F)(using interpreter: Interpreter[F, G]): G =
    interpreter.interpret(source)
