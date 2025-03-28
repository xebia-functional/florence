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

package florence.core.dsl.styling

import florence.core.model.ChartDef
import florence.core.model.ChartDef.LineChart
import florence.core.model.styling.ChartStyleDef
import florence.core.model.styling.ChartStyleDef.LineChartStyle

final case class StyledChart[C <: ChartDef, S <: ChartStyleDef](
    chart: C,
    style: S
)
