package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import kotlinx.coroutines.*

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.utils.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Servos.*

@Autonomous(name = "Autonomous", group = "Rover Ruckus")
class Auto : LinearOpMode() {
    val robot = Robot(this)

    override fun runOpMode() = runBlocking {
        //============
        //=== INIT ===
        //============
        robot.init()
        robot.initVuforia()

        robot.servos[Phone]?.position = 0.35

        //======================
        //=== WAIT FOR START ===
        //======================
        waitForStartFixed()

        //===============
        //=== LANDING ===
        //===============
        robot.servos[Phone]!!.position = 0.5

        robot.moveLifting(up = true)
        robot.moveByDistance(-10.0)

        runParallel {
            robot.moveLifting(up = false)
        }

        robot.strafeByDistance(-15.0, 0.75)
        robot.moveByDistance(10.0, 0.75)


        //================
        //=== SAMPLING ===
        //================
        robot.initTfod()
        var goldPos = robot.recognizeGold()

        robot.strafeByDistance(-15.0, 0.75)
        robot.strafeByDistance(7.5, 0.75)

        when (goldPos) {
            GoldPos.Left -> {
                robot.moveByDistance(-65.0, 0.75)
            }
            GoldPos.Middle -> {
                robot.moveByDistance(-110.0, 0.75)
            }
            GoldPos.Right -> {
                robot.moveByDistance(-160.0, 0.75)
            }
        }


        //===================
        //=== ORIENTATION ===
        //===================
        robot.rotate(40.0, 0.3)

        val depotPos = robot.recognizeVuMark()

        robot.strafeByDistance(-10.0, 0.75)

        when (depotPos) {
            DepotPos.Left -> {
                robot.moveByDistance(-150.0, 0.8)
                robot.strafeByDistance(-10.0)
                wait(1.0)
                robot.moveByDistance(200.0, 0.8)
            }
            DepotPos.Right -> {

            }
        }

        //================
        //=== CLAIMING ===
        //================

        //===============
        //=== PARKING ===
        //===============


        //==============
        //=== FINISH ===
        //==============
        finishAllJobs()

        //============
        //=== STOP ===
        //============

    }
}