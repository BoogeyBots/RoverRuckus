package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

enum class GoldPos {
    Left,
    Middle,
    Right
}

fun Robot.recognizeGold(): GoldPos {
    return GoldPos.Middle
}

fun Robot.recognizeVuMark() {

}