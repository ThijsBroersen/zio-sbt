package zio.sbt

import sbt.Keys._
import sbt._

object SharedTasksAndSettings extends AutoPlugin {

  override def trigger = allRequirements

  override def requires: Plugins = super.requires

  object autoImport {

    val welcomeBannerEnabled: SettingKey[Boolean] =
      settingKey[Boolean]("Indicates whether or not to enable the welcome banner.")

    val banners: SettingKey[Seq[String]] = settingKey[Seq[String]](
      "A list of banners that will be displayed as part of the welcome message."
    )

    val welcomeTaskAndSettingsEnabled: SettingKey[Boolean] =
      settingKey[Boolean]("Indicates whether or not to enable the welcome task and settings.")

    val usefulTasksAndSettings: SettingKey[Seq[(String, String)]] = settingKey[Seq[(String, String)]](
      "A map of useful tasks and settings that will be displayed as part of the welcome banner."
    )
  }

  import autoImport.*

  private val allAggregates =
    ScopeFilter(inAggregates(ThisProject))

  val allBanners: Def.Initialize[Seq[String]] = Def.settingDyn {
    Def.setting(
      banners
        .all(allAggregates)
        .value
        .foldLeft(Seq.empty[String])(_ ++ _)
    )
  }

  val allUserfulTasksAndSettings: Def.Initialize[Seq[(String, String)]] =
    Def.settingDyn {
      Def.setting(
        usefulTasksAndSettings
          .all(allAggregates)
          .value
          .foldLeft(Seq.empty[(String, String)])(_ ++ _)
      )
    }

  override def projectSettings: Seq[Setting[_]] = Seq(
    banners                := Seq.empty,
    usefulTasksAndSettings := Seq.empty
  )

  override def globalSettings: Seq[Def.Setting[_]] = super.globalSettings ++
    Seq(
      welcomeBannerEnabled          := true,
      welcomeTaskAndSettingsEnabled := true,
      banners                       := Seq.empty,
      usefulTasksAndSettings        := Seq.empty,
      Global / excludeLintKeys += usefulTasksAndSettings
    )

}
