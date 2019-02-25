package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForStartFixed

@Autonomous(name = "Autonomous", group = "Rover Ruckus")
class Auto : LinearOpMode() {
    val robot = Robot(this)

    override fun runOpMode() {
        //============
        //=== INIT ===
        //============
        robot.init()

        //======================
        //=== WAIT FOR START ===
        //======================
        waitForStartFixed()

        //===========
        //=== RUN ===
        //===========

        // TODO land
        // TODO recognize gold
        // TODO go forward near sampling field
        // TODO move gold
        // TODO move to wall
        // TODO read picture
        // TODO todo go to depot
        // TODO todo drop team marker
        // TODO go to crater
        // TODO park

        //============
        //=== STOP ===
        //============

    }
}