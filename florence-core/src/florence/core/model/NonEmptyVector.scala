package florence.core.model

opaque type NonEmptyVector[A] = Vector[A]

object NonEmptyVector:

  def apply[A](head: A, tail: Vector[A]): NonEmptyVector[A] =
    head +: tail

  def apply[A](head: A, tail: A*): NonEmptyVector[A] =
    val builder = Vector.newBuilder[A]
    builder += head
    tail.foreach(builder += _)
    builder.result()

  def fromVector[A](vector: Vector[A]): Option[NonEmptyVector[A]] =
    Option.when(vector.nonEmpty)(vector)

  extension [A](nonEmptyVector: NonEmptyVector[A]) def toVector: Vector[A] = nonEmptyVector
