package florence.core.model

/** Represents all the valid types that we can handle as data points in the LineChart
  */
enum Domain[Type]:
  /** Evidence that [[Type]] is equal to one of the valid Domain Types.
    * This is useful to convert from a generic [[Type]] to a more specific [[Domain.Types]]
    * (and vice versa)
    */
  val eq: Type EqualToEither Domain.Types

  case Reals[Type](override val eq: Type =:= Double)    extends Domain[Type]
  case Discrete[Type](override val eq: Type =:= String) extends Domain[Type]

object Domain:
  /** All valid Domain types
    */
  type Types = Double *: String *: EmptyTuple

  given reals[Type](using eq: Type =:= Double): Domain[Type] =
    Reals(eq)

  given discrete[Type](using eq: Type =:= String): Domain[Type] =
    Discrete(eq)

/** {{{
  *
  *   EqualToEither[A, T1 *: T2 *: ... *: TN *: EmptyTuple]
  * = (A =:= T1 | A =:= T2 | ... | A =:= TN)
  * }}}
  */
infix type EqualToEither[A, Types <: Tuple] =
  Tuple.Union[Tuple.Map[Types, [X] =>> A =:= X]]
