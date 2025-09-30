/*
 * Copyright 2025 Xebia Functional Open Source <https://www.xebia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package florence.core.model.shared

// Shared types for styling and rendering

object StyleTypes:

  enum Alignment:
    case Left, Center, Right

  enum LegendPosition:
    case Top, Right, Bottom, Left, TopRight, TopLeft, BottomRight, BottomLeft

  enum LineType:
    case Solid, Dashed, Dotted, DashDot

  enum MarkerType:
    case None, Circle, Square, Triangle, Star, Plus

  enum SliceLabelPosition:
    case Inside, Outside, None

  final case class FontSpec(
      family: String,
      size: Double,
      weight: String
  )

  object FontSpec:

    val default: FontSpec =
      FontSpec(
        family = "sans-serif",
        size = 12.0,
        weight = "normal"
      )

    def apply(family: String): FontSpec =
      FontSpec(
        family,
        12.0,
        "normal"
      )

    def apply(family: String, size: Double): FontSpec =
      FontSpec(
        family,
        size,
        "normal"
      )

  final case class LineStyle(
      colour: String,
      width: Double,
      dash: Option[List[Double]]
  )

  object LineStyle:
    def apply(colour: String): LineStyle =
      LineStyle(colour, 1.0, None)

    def apply(colour: String, width: Double): LineStyle =
      LineStyle(colour, width, None)

    def apply(colour: String, width: Double, dash: List[Double]): LineStyle =
      LineStyle(colour, width, Some(dash))

  final case class Padding(
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  )

  object Padding:

    val default: Padding =
      Padding(
        top = 10.0,
        right = 10.0,
        bottom = 10.0,
        left = 10.0
      )

  final case class Margins(
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  )

  object Margins:

    val default: Margins =
      Margins(
        top = 40,
        right = 40,
        bottom = 50,
        left = 50
      )
