/*
 * Copyright 2022-2023 dev.zio
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

package zio.sbt

import sbt.Keys._
import sbt.{JavaVersion => _, _}

import zio.sbt.SharedTasksAndSettings.autoImport.{banners, usefulTasksAndSettings}

object ZioSbtCrossbuildPlugin extends AutoPlugin {

  override def trigger = allRequirements

  override def requires: Plugins = super.requires && SharedTasksAndSettings

  object autoImport extends PlatformAssertions.Keys with CompileTasks.Keys with TestTasks.Keys {

    def zioSbtCrossbuildSettings: Seq[Setting[_]] =
      PlatformAssertions.settings ++
        inConfig(Compile)(CompileTasks.settings) ++
        inConfig(Test)(CompileTasks.settings) ++
        inConfig(Test)(TestTasks.settings)
  }

  import autoImport.*

  override def projectSettings: Seq[Setting[_]] = zioSbtCrossbuildSettings ++ Seq(
    banners ++= {
      if (SharedTasks.isRoot.value) PlatformAssertions.docs.value
      else Seq.empty
    },
    usefulTasksAndSettings ++= {
      if (SharedTasks.isRoot.value) {
        CompileTasks.docs.value ++ TestTasks.docs.value
      } else Seq.empty
    }
  )

  override def buildSettings: Seq[Def.Setting[_]] = super.buildSettings ++ Set(
    scala212 := ScalaVersions.scala212,
    scala213 := ScalaVersions.scala213,
    scala3   := ScalaVersions.scala3,
    javaPlatform := {
      val targetJVM       = javaPlatform.?.value.getOrElse(JavaVersion.`11`)
      val jdkNeedsUpgrade = currentJDK.value.toInt < targetJVM.toInt
      if (jdkNeedsUpgrade)
        sLog.value.warn(
          s"\u001b[33mJDK upgrade is required, target ($targetJVM) is higher than the current JDK version (${currentJDK.value}), compilation will fail!\u001b"
        )
      targetJVM
    }
  )

  override def globalSettings: Seq[Def.Setting[_]] =
    super.globalSettings ++ Seq(
      currentJDK := sys.props("java.specification.version"),
      PlatformAssertions.allScalaVersionsSetting
    )
}
